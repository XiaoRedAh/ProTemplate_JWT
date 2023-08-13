package com.xiaoRed.controller;

import com.xiaoRed.entity.RestBean;
import com.xiaoRed.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于验证相关的Controller，包含用户的注册、重置密码等操作
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthorizeController {

    @Resource
    AccountService accountService;

    /**
     * 请求邮件验证码
     * @param type 类型， 必须是register或reset中的一个
     * @param email 请求邮件，必须是合法的邮箱地址才能通过校验
     * @param request 请求，用来获得请求的ip地址
     * @return 是否请求成功
     */
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Pattern(regexp = "(register|reset)") String type, @RequestParam @Email String email,
                                        HttpServletRequest request){
        String message = accountService.sendEmailVerifyCode(type, email, request.getRemoteAddr());
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
