package io.nuri.user_service;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class Demo {


    public static void main(String[] args)  {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"admin\":true}";

        String encodedHeader = base64URLEncode(header);
        String encodedPayload = base64URLEncode(payload);

        System.out.println("Encoded Header: " + encodedHeader);
        System.out.println("Encoded Payload: " + encodedPayload);


        SecretKey key = Jwts.SIG.HS256.key().build();

        String jws = Jwts.builder().subject("Joe").signWith(key).compact();

        assert Jwts.parser().verifyWith(key).build().parseSignedClaims(jws)
                .getPayload().getSubject().equals("Joe");


        System.out.println("++++++++++++++++++++");

        String jwt = generateJwt(null, key);

        System.out.println("Stirng " + jwt);

        Jws<Claims> parsed = parser(jwt, key);
        System.out.println("parser: " + parsed.toString());

        Claims claims = parsed.getPayload();
        System.out.println(claims);

        System.out.println("name is " + claims.get("name"));
        System.out.println("role is " + claims.get("role"));


    }

    public static String base64URLEncode(String input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes());
    }

    public static String generateJwt(Map<String, Object> map, SecretKey key){
        return Jwts.builder()
//                .header()
//                    //.add(map)
//                    .add(header("Nur", "Admin", "mail"))
//                    .and()
//                .subject("Joe")
//                .claims(map)
                .claim("name", "Nur")
                .claim("role", "Admin")
                .expiration(new Date(System.currentTimeMillis() * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }

    public  static Jws<Claims> parser(String jwt, SecretKey key){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt);
    }
    public static Header header(String name, String role, String email){
        return Jwts.header()
                .add("name", name)
                .add("email", email)
                .add("role", role)
                .build();
    }
}


