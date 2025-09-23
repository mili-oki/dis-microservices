package com.example.dis.auth_service.repository;

import com.example.dis.auth_service.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
  boolean existsByUsername(String username);
  Optional<UserAccount> findByUsername(String username);
}
