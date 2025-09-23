package com.example.dis.auth_service.web;

import com.example.dis.auth_service.service.AuthService;
import com.example.dis.auth_service.web.dto.LoginRequest;
import com.example.dis.auth_service.web.dto.LoginResponse;
import com.example.dis.auth_service.web.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody RegisterRequest req) {
    service.register(req.getUsername(), req.getPassword());
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest req) {
    String token = service.login(req.getUsername(), req.getPassword());
    return new LoginResponse(token);
  }
}
