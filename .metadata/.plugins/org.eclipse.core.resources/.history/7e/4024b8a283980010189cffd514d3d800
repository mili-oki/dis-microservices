package com.example.dis.auth_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String passwordHash; // BCrypt hash

  // npr. "ROLE_USER" ili "ROLE_ADMIN,ROLE_USER"
  @Column(nullable = false)
  private String roles;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getRoles() { return roles; }
  public void setRoles(String roles) { this.roles = roles; }
}
