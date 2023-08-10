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

    /**
     * 解析jwt令牌，如果没有问题，则返回令牌；如果有问题(过期，被篡改，被拉黑...)则返回空
     * @param authorization Authorization请求头中携带的内容
     * @return DecodedJWT
     */
    public DecodedJWT resolveJwt(String authorization){
        String token = this.convertToken(authorization);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(JwtConstant.JWT_SECRET);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{//如果验证后发现jwt有问题，需要抛异常且返回空
            DecodedJWT decodedJWT = jwtVerifier.verify(token);//对JWT令牌进行验证，看看是否被修改
            Date expireAt = decodedJWT.getExpiresAt();//拿到令牌的失效时间
            return new Date().after(expireAt) ? null : decodedJWT;//如果令牌现在已经失效，返回空；否则返回令牌
        }catch(JWTVerificationException e){//这是运行时异常不会显式地抛出，需要手动捕获
            return null;
        }
    }

    /**
     * 将Authorization请求头中的JWT令牌拿出来
     * @param authorization Authorization请求头中携带的内容
     * @return 令牌
     */
    public String convertToken(String authorization){
        //如果authorization为空或不是以"Bearer "开头，则没有携带JWT令牌，直接返回空
        if(authorization == null || !authorization.startsWith("Bearer "))
            return null;
        return authorization.substring(7);//将前7个字符切割掉，留下JWT令牌

    }

    /**
     * 从解析好的jwt中拿出用户信息，封装为UserDetails
     * @param decodedJWT 已解析的Jwt对象
     * @return UserDetails
     */
    public UserDetails toUser(DecodedJWT decodedJWT){
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("name").toString())
                .password("*******")//createJWT方法在生成jwt时没有设置password负载，这里随便写一个
                .authorities(claims.get("authorities").toString())
                .build();
    }

}
