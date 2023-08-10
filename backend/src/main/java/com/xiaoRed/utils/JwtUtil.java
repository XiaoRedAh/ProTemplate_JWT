package com.xiaoRed.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xiaoRed.constants.JwtConstant;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Jwt工具类
 */
@Component
public class JwtUtil {

    /**
     * 根据UserDetails生成对应的Jwt令牌
     * @return 令牌
     */
    public String createJWT(UserDetails details, int id, String username){
        Algorithm algorithm = Algorithm.HMAC256(JwtConstant.JWT_SECRET);//设置加密算法
        return JWT.create()
                //给jwt设置一个随机的uuid，用于唯一标识，在黑名单中有用
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withIssuedAt(new Date())//设置生效起始时间,现在生效
                .withExpiresAt(generateExpirationDate())//设置失效时间
                .sign(algorithm);//最后按照指定算法签发令牌
    }

    /**
     * 计算JWT失效时间：当前时间往后3天
     * @return 过期时间
     */
    public Date generateExpirationDate() {
        //失效时间是当前时间（签发令牌的时间）+(常量类中定义的时间)*3
        return new Date(System.currentTimeMillis() + 3 * JwtConstant.JWT_EXPIRATION);
    }

}
