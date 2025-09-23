package com.example.dis.auth_service.service;

import com.example.dis.auth_service.model.AppUser;
import com.example.dis.auth_service.repository.AppUserRepository;
import com.example.dis.auth_service.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

  private final AppUserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtUtil jwt;

  public AuthService(AppUserRepository repo, PasswordEncoder encoder, JwtUtil jwt) {
    this.repo = repo;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @Transactional
  public void register(String username, String rawPassword) {
    if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
    }
    if (repo.existsByUsername(username)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username taken");
    }
    AppUser u = new AppUser();
    u.setUsername(username);
    u.setPasswordHash(encoder.encode(rawPassword));
    u.setRoles("ROLE_USER");
    repo.save(u);
  }

  public String login(String username, String rawPassword) {
    AppUser u = repo.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));
    if (!encoder.matches(rawPassword, u.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
    }
    List<String> roles = Arrays.asList(u.getRoles().split(","));
    return jwt.generate(u.getUsername(), roles);
  }
}
