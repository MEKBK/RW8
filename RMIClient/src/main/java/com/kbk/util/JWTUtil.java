package com.kbk.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/03/28 21:56
 * @Version 1.0
 *
 */

public class JWTUtil {

    /**
     * 生成jwt的方法
     * @param date 签发时间
     * @param secrety 私钥
     * @param
     * @return
     */
    public static String getJWT(String id, String userName, Date date, String secrety){

        JwtBuilder jwtBuilder= Jwts.builder().setId(id).setIssuer(userName)
                .setIssuedAt(date)
                .signWith(SignatureAlgorithm.HS256,secrety);
        String token = jwtBuilder.compact();
        return token;
    }

    /**
     * 解析token
     * @param token token
     * @param secrety 私钥
     * @return
     */
    public static Claims parseJWT(String token, String secrety){
        Claims claims=Jwts.parser().setSigningKey(secrety).parseClaimsJws(token).getBody();
        return claims;
    }



}

