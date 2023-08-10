package com.xiaoRed.config;

import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.vo.response.AuthorizeVo;
import com.xiaoRed.service.AccountService;
import com.xiaoRed.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtil jwtUtil;

    @Resource
    AccountService accountService;

    //创建一个BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf ->conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure))
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .csrf(AbstractHttpConfigurer::disable)
                //采用JWT方案，不用session了
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    //认证成功处理器
    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        //从这里拿到loadUserByUsername方法返回的用户信息
        User details = (User) authentication.getPrincipal();
        /*在实现的loadUserByUsername方法中，放在User的username里的可能是Account的username，也可能是email
        因此要想得到Account的username，不能用details.getUsername()，这里有两种方案：
        方案一： 再用findAccountByNameOrEmail方法查一次用户，通过它来获得username【这样的话，登录就要查两次数据库，而且是一样的结果】
        方案二： 采用ThreadLocal
        这里采用方案一
         */
        Account account = accountService.findAccountByNameOrEmail(details.getUsername());
        String token = jwtUtil.createJWT(details, account.getId(), account.getUsername());
        AuthorizeVo authorizeVo = new AuthorizeVo();
        authorizeVo.setUsername(account.getUsername());
        authorizeVo.setRole(account.getRole());
        authorizeVo.setToken(token);
        authorizeVo.setExpire(jwtUtil.generateExpirationDate());
        response.getWriter().write(RestBean.success(authorizeVo).asJsonString());
    }

    //认证失败处理器
    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
    }

    //退出登录成功处理器
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

    }
}