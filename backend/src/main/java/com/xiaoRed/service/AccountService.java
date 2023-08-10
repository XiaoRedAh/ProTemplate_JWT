package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * (Account)表服务接口
 *
 * @author makejava
 * @since 2023-08-09 10:12:43
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    /**
     * 通过用户名/邮箱找到数据库中对应的用户
     */
    public Account findAccountByNameOrEmail(String text);

}

