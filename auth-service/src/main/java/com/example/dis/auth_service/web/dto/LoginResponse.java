package com.example.dis.auth_service.web.dto;

public class LoginResponse {
  private String token;

  public LoginResponse() {}
  public LoginResponse(String token) { this.token = token; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }
}
