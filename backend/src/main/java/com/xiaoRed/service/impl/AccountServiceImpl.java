package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.mapper.AccountMapper;
import com.xiaoRed.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2023-08-09 10:12:45
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

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
}

