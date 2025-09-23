package com.example.dis.auth_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

  @Value("${auth.jwt.secret}")
  private String secret;

  @Value("${auth.jwt.issuer}")
  private String issuer;

  @Value("${auth.jwt.expires-min:120}")
  private long expiresMinutes;

  public String generate(String subject, List<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setSubject(subject)
        .setIssuer(issuer)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(expiresMinutes * 60)))
        .claim("roles", roles)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }
}
