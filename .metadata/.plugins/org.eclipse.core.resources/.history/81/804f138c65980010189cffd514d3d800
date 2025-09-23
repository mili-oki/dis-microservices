package com.example.dis.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import java.util.Date;

@Component
public class JwtUtil {

  @Value("${auth.jwt.secret}")
  private String secret;

  @Value("${auth.jwt.issuer}")
  private String issuer;

  @Value("${auth.jwt.expires-min}")
  private long expiresMin;

  public String generate(String username, List<String> roles) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expiresMin * 60_000);

    return Jwts.builder()
        .setSubject(username)
        .setIssuer(issuer)
        .setIssuedAt(now)
        .setExpiration(exp)
        .claim("roles", roles)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseClaimsJws(token);
  }

  @SuppressWarnings("unchecked")
  public List<String> extractRoles(Claims claims) {
    Object raw = claims.get("roles");
    if (raw instanceof List<?> list) {
      return list.stream().map(Object::toString).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
