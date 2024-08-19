package com.xing.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {
    //设置过期时间
    private static final long EXPIRE_DATE = 30 * 60 * 100000;
    //token秘钥
    private static final String TOKEN_SECRET = "Changge";

    /**
     * 加密Token
     *
     */
    public static String token(String nickName) {
        String token;
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            //携带UserId信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("nickName", nickName)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    /**
     * JWT解密
     *
     */
    public static String decodeToken(final String token) {
        String nickName = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET))
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt != null) {
                nickName = String.valueOf(jwt.getClaim("nickName").asString());
            }
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return nickName;
    }

    /**
     * 验证Token
     *
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
