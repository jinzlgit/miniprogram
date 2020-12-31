package com.king.framework.shiro.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/16 10:34
 */
public class TokenUtil {

    private static final Logger log = LoggerFactory.getLogger(TokenUtil.class);

    // Token到期时间为5分钟，单位毫秒
    public static final long EXPIRE_TIME = 5 * 60 * 1000;

    // refreshToken到期时间为30分钟，单位秒
    public static final long REFRESH_EXPIRE_TIME = 30 * 60;

    // 秘钥盐
    private static final String TOKEN_SECRET = "ljdyaishijin**3nkjnj??";

    /**
     * 生成Token
     *
     * @param account
     * @param currentTime
     * @return java.lang.String
     * @author 金振林
     * @date 2020/12/16 10:44
     */
    public static String sign(String account, Long currentTime) {
        String token = null;
        try {
            Date expireAt = new Date(currentTime + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0") // 发行人
                    .withClaim("account", account) // 存放数据
                    .withClaim("currentTime", currentTime)
                    .withExpiresAt(expireAt) // 过期时间
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JWTCreationException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * Token验证
     *
     * @param token
     * @return java.lang.Boolean
     * @author 金振林
     * @date 2020/12/16 10:53
     */
    public static Boolean verify(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = jwtVerifier.verify(token);
        } catch (Exception e) {
            return false;
        }
        log.info("Token验证通过：");
        log.info("account:[{}]", decodedJWT.getClaim("account").asString());
        log.info("过期时间:[{}]", decodedJWT.getExpiresAt());
        return true;
    }

    public static String getAccount(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("account").asString();
        } catch (JWTCreationException e) {
            return null;
        }
    }

    public static Long getCurrentTime(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("currentTime").asLong();
        } catch (JWTCreationException e) {
            return null;
        }
    }

    // public static void main(String[] args) {
    //     String token = JWT.create()
    //             .withIssuer("auth0") // 发行人
    //             .withClaim("account", account) // 存放数据
    //             .withClaim("currentTime", currentTime)
    //             .withExpiresAt(expireAt) // 过期时间
    //             .sign(Algorithm.HMAC256("123456"));
    // }

}
