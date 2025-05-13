package com.AJA.Interview.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
	
	private final String secretKey;


    public JwtService() {
        SecretKey sk = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    }
    
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey1234"; // must be 256 bits for HS256
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    
    

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                              .setSigningKey(key)
                              .build()
                              .parseClaimsJws(token)
                              .getBody()
                              .getExpiration();
        return expiration.before(new Date());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                   .setSubject(email)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                   .signWith(key)
                   .compact();
    }

}
