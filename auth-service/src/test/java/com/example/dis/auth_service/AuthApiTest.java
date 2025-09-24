package com.example.dis.auth_service;

import com.example.dis.auth_service.domain.UserAccount;
import com.example.dis.auth_service.repository.UserAccountRepository;
import com.example.dis.auth_service.security.JwtUtil;
import com.example.dis.auth_service.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthApiTest {

    private final UserAccountRepository repo = mock(UserAccountRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final JwtUtil jwt = mock(JwtUtil.class);

    private final AuthService service = new AuthService(repo, encoder, jwt);

    @Test
    void register_persists_user_with_encoded_password_and_role() {
        when(repo.existsByUsername("testuser")).thenReturn(false);
        when(encoder.encode("pwd")).thenReturn("ENC_HASH");
        // simuliraj save koji dodeli ID
        when(repo.save(any(UserAccount.class))).thenAnswer(inv -> {
            UserAccount u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserAccount saved = service.register("testuser", "pwd");

        assertThat(saved.getId()).isEqualTo(1L);
        verify(repo).existsByUsername("testuser");
        verify(encoder).encode("pwd");
        verify(repo).save(argThat(u ->
                "testuser".equals(u.getUsername())
                        && "ENC_HASH".equals(u.getPasswordHash())
                        && "ROLE_USER".equals(u.getRoles())
        ));
    }

    @Test
    void register_throws_when_username_taken() {
        when(repo.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> service.register("john", "pwd"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Username taken");

        verify(repo, never()).save(any());
    }

    @Test
    void login_returns_jwt_on_valid_credentials() {
        UserAccount u = new UserAccount();
        u.setUsername("john");
        u.setPasswordHash("ENC");

        when(repo.findByUsername("john")).thenReturn(Optional.of(u));
        when(encoder.matches("pwd", "ENC")).thenReturn(true);
        when(jwt.generate("john", List.of("ROLE_USER"))).thenReturn("dummy.jwt.token");

        String token = service.login("john", "pwd");

        assertThat(token).isEqualTo("dummy.jwt.token");
        verify(repo).findByUsername("john");
        verify(encoder).matches("pwd", "ENC");
        verify(jwt).generate("john", List.of("ROLE_USER"));
    }

    @Test
    void login_throws_on_bad_password() {
        UserAccount u = new UserAccount();
        u.setUsername("john");
        u.setPasswordHash("ENC");

        when(repo.findByUsername("john")).thenReturn(Optional.of(u));
        when(encoder.matches("bad", "ENC")).thenReturn(false);

        assertThatThrownBy(() -> service.login("john", "bad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bad credentials");
    }

    @Test
    void login_throws_when_user_not_found() {
        when(repo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("ghost", "pwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bad credentials");
    }
}
