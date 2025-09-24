package com.example.dis.auth_service;

import com.example.dis.auth_service.domain.UserAccount;
import com.example.dis.auth_service.repository.UserAccountRepository;
import com.example.dis.auth_service.security.JwtUtil;
import com.example.dis.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UserAccountRepository repo = mock(UserAccountRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final JwtUtil jwt = mock(JwtUtil.class);

    private final AuthService service = new AuthService(repo, encoder, jwt);

    @Test
    void login_returns_token_when_user_exists() {
        String username = "user1";
        String raw = "pwd";
        String storedHash = "HASHED";

        UserAccount ua = new UserAccount();
        ua.setId(1L);
        ua.setUsername(username);
        ua.setPasswordHash(storedHash);

        when(repo.findByUsername(username)).thenReturn(Optional.of(ua));
        when(encoder.matches(raw, storedHash)).thenReturn(true);
        when(jwt.generate(eq(username), anyList())).thenReturn("dummy.jwt.token");

        String token = service.login(username, raw);

        assertThat(token).isEqualTo("dummy.jwt.token");
        verify(repo).findByUsername(username);
        verify(encoder).matches(raw, storedHash);
        verify(jwt).generate(eq(username), anyList());
    }

    @Test
    void register_persists_user() {
        UserAccount ua = new UserAccount();
        ua.setId(2L);
        when(repo.save(any(UserAccount.class))).thenReturn(ua);
        // ako register enkoduje lozinku, mockuj encoder.encode(...)
        when(encoder.encode(anyString())).thenReturn("ENC_HASH");

        UserAccount saved = service.register("x@y.com", "pwd");
        assertThat(saved.getId()).isEqualTo(2L);
        verify(repo).save(any(UserAccount.class));
    }
}
