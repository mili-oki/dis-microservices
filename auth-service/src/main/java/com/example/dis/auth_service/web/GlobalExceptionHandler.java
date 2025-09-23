package com.example.dis.auth_service.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> conflict(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
      "timestamp", OffsetDateTime.now().toString(),
      "status", 409,
      "error", "Username taken"
    ));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> unauthorized(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
      "timestamp", OffsetDateTime.now().toString(),
      "status", 401,
      "error", "Bad credentials"
    ));
  }
}
