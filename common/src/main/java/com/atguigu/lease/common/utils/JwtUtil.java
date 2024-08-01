package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * ClassName: JwtUtil
 * PackageName: com.atguigu.lease.common.utils
 * Create: 2024/7/31-14:04
 * Description: JWT工具类，方法有创建token
 */
public class JwtUtil {
    private static final SecretKey key = Keys.hmacShaKeyFor("G6xxdXNg6TzKLC9wCmxi8J9oc0KV4PtM".getBytes()); // 32位密码也就是，256byte，符合安全要求
    public static String createToken(Long userId,String userName){
        // 将token设置成有效期为1分钟
       return Jwts.builder().setExpiration(new Date(System.currentTimeMillis() + 3600000)).
                setSubject("LOGIN_USER").
                claim("userId",userId).
                claim("username",userName).
                signWith(key, SignatureAlgorithm.HS256).compact();

    }

    // 解析token
    // 原理就是，根据客户端传来的token中的header和payload，以及我们提供的signingKey，生成signature然后与客户端传来的token的签名进行进行匹对，如果一样就是合法的
    public static Claims parseToken(String token){
        if (token == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
            Claims claims = parser.parseClaimsJws(token).getBody();
            return claims;
        }catch (ExpiredJwtException expiredJwtException){
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        }catch (JwtException jwtException){
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }

    }

    public static void main(String[] args) {
        System.out.println(createToken(2L,"user"));
    }
}
