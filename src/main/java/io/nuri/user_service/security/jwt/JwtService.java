package io.nuri.user_service.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static SecretKey secretKey;

    public static String getEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getAuthorities())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(getKey())
                .compact();
    }

    public static String generateOneTimeToken(String name, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("name", name)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getKey())
                .compact();
    }

    public static Claims getClaimsOfOneTimeToken(String oneTimeToken) {
        return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(oneTimeToken)
                    .getPayload();
    }

    public static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public static Authentication getAuthentication(String token, UserDetails userDetails){
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }



    private static SecretKey getKey(){
        if(secretKey == null){
            byte[] keyBytes = Decoders.BASE64.decode("SIGNINGKEYSIGNINGKEYSIGNINGKEYSIGNINGKEYSIGNINGKEYSIGNINGKEYSIGNINGKEYSIGNINGKEY");
            secretKey =  Keys.hmacShaKeyFor(keyBytes);
        }
        return secretKey;
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private static  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }
}
