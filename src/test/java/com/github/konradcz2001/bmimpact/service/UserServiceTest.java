package com.github.konradcz2001.bmimpact.service;

import com.github.konradcz2001.bmimpact.dto.ChangePasswordDto;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePassword() {
        // Given
        String username = "testUser";
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("oldPass");
        dto.setNewPassword("NewPass123!");
        dto.setConfirmNewPassword("NewPass123!");

        User user = new User(username, "encodedOldPass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("encodedNewPass");

        // When
        userService.changePassword(username, dto);

        // Then
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPass");
    }

    @Test
    @DisplayName("Should throw exception when current password does not match")
    void shouldThrowExceptionWhenCurrentPasswordIsInvalid() {
        // Given
        String username = "testUser";
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("wrongPass");

        User user = new User(username, "encodedOldPass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(username, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid current password");
    }

    @Test
    @DisplayName("Should delete account and all associated results")
    void shouldDeleteAccount() {
        // Given
        String username = "userToDelete";
        User user = new User(username, "pass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        userService.deleteAccount(username);

        // Then
        verify(bmiResultRepository).deleteByUsername(username);
        verify(userRepository).delete(user);
    }
}