package com.example.routineapp.service;

import com.example.routineapp.entity.User;
import com.example.routineapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("register")
    class Register {
        @Test
        void 新規ユーザーが登録される() {
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded");
            User saved = new User();
            saved.setId(1L);
            saved.setEmail("new@example.com");
            saved.setName("新規ユーザー");
            saved.setPassword("encoded");
            when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

            User result = userService.register("new@example.com", "password123", "新規ユーザー");

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("new@example.com");
            assertThat(result.getName()).isEqualTo("新規ユーザー");
            verify(userRepository).existsByEmail("new@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(u ->
                    "new@example.com".equals(u.getEmail()) && "encoded".equals(u.getPassword()) && "新規ユーザー".equals(u.getName())
            ));
        }

        @Test
        void 同一メールが既に存在する場合は例外() {
            when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.register("exists@example.com", "pass", "名前"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("既に登録されています");
            verify(userRepository).existsByEmail("exists@example.com");
        }
    }
}
