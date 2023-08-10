package com.xiaoRed.constants;
/**
 * JWT常量类
 */
public class JwtConstant {

    //jwt密钥，随便设，建议长一点
    public static final String JWT_SECRET = "asdadevvefe";

    //jwt失效时间，单位秒。这里一共是24小时
    public static final Long JWT_EXPIRATION = 24 * 60 * 60 * 1000L;

    //存放在redis中的黑名单的key
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
}
