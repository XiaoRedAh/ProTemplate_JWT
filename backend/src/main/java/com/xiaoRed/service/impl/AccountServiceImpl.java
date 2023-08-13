package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.constants.EmailConstant;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.mapper.AccountMapper;
import com.xiaoRed.service.AccountService;
import com.xiaoRed.utils.FlowUtil;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2023-08-09 10:12:45
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtil flowUtil;

    /**
     *实现loadUserByUsername方法
     *  1. 从数据库查询用户信息（登录功能）
     *  2. 如果查到，就再去查询对应的权限信息（授权功能）
     *  3. 封装成UserDetails对象返回（这要求再去创建一个UserDetails的实现类）
     *  注意：由于实现通过用户名/邮箱登录，因此这里传进来的username参数实际上有可能是邮箱
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        //这个User是SpringSecurity提供的
        /* 这里也可以专门封装一个LoginAccount实体类实现UserDetails，
        User要作为这个实体类的属性成员，在下面返回时返回一个LoginAccount对象*/
        return User
                //实际上，放在这个User的username里的可能是Account的username，也可能是email
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 通过用户名/邮箱找到数据库中对应的用户，提供给上面的loadUserByUsername方法
     */
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    /**
     * 发送邮箱验证码
     * @param type 判断在哪个场景下发送验证码：注册邮箱，重置密码，...
     * @param email 将验证码发送给哪个邮箱
     * @param ip 不能一直请求，需要记录ip地址限制请求频率
     * @return 返回null表示发送成功
     */
    @Override
    public String sendEmailVerifyCode(String type, String email, String ip) {
        //加一把锁，防止同一时间被多次调用，保证同一个IP发送的请求需要排队
        synchronized (ip.intern()){
            if(!this.verifyLimit(ip))//verifyLimit方法返回false，说明该ip已经被限流了
                return "请求频繁，请稍后再试";
            //生成6位验证码
            Random random =new Random();
            int code = random.nextInt(899999) + 100000;//这样保证生成的code一定是6位数
            //将"类型，收件人，验证码"分装在一个map中，作为消息，发送到"mail"消息队列中
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            //将key前缀:目标邮箱作为key，验证码作为value存入redis，设置3分钟有效
            //后续用户提交填写的验证码，就是和这个redis中的做比较
            stringRedisTemplate.opsForValue()
                    .set(EmailConstant.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     * @param ip 请求的ip地址
     * @return 是否通过限流验证验证，false表示该ip在限流名单中，true表示尚未被限流
     */
    private boolean verifyLimit(String ip) {
        String key = EmailConstant.VERIFY_EMAIL_LIMIT + ip;
        return flowUtil.limitOnceCheck(key, 60);//限流的冷却时间设置为1分钟
    }


}

