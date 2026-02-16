package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.UserRegistrationDto;
import com.github.konradcz2001.bmimpact.model.User;
import com.github.konradcz2001.bmimpact.repository.BmiResultRepository;
import com.github.konradcz2001.bmimpact.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BmiResultRepository bmiResultRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register new user with encoded password")
    void shouldRegisterNewUser() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("newUser");
        dto.setPassword("Secret123!");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        // When
        userService.registerUser(dto);

        // Then
        verify(passwordEncoder, times(1)).encode("Secret123!");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals("newUser") &&
                        user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    @DisplayName("Should throw exception if username already exists")
    void shouldThrowExceptionIfUserExists() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("existingUser");
        dto.setPassword("Pass123!");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already exists");

        verify(userRepository, never()).save(any());
    }
}