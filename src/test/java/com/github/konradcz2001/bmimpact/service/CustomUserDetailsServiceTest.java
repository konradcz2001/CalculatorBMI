package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.model.User;
import com.github.konradcz2001.bmimpact.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user by username when user exists")
    void shouldLoadUserByUsername() {
        // Given
        User user = new User("activeUser", "encodedPassword");
        when(userRepository.findByUsername("activeUser")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("activeUser");

        // Then
        assertThat(userDetails.getUsername()).isEqualTo("activeUser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown");
    }
}