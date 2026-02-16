package com.github.konradcz2001.bmimpact.controller;

import com.github.konradcz2001.bmimpact.model.User;
import com.github.konradcz2001.bmimpact.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should load profile page for authenticated user")
    void shouldLoadProfilePage() throws Exception {
        // When & Then
        mockMvc.perform(get("/profile")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("passwordDto"));
    }

    @Test
    @DisplayName("Should change password successfully")
    void shouldChangePassword() throws Exception {
        // Given
        User user = new User("testUser", passwordEncoder.encode("OldPass123!"));
        userRepository.save(user);

        // When & Then
        mockMvc.perform(post("/profile/change-password")
                        .with(user("testUser").roles("USER"))
                        .with(csrf())
                        .param("currentPassword", "OldPass123!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("successMessage", "Password changed successfully."));
    }

    @Test
    @DisplayName("Should fail password change if current password is wrong")
    void shouldFailIfCurrentPasswordIsWrong() throws Exception {
        // Given
        User user = new User("testUser", passwordEncoder.encode("OldPass123!"));
        userRepository.save(user);

        // When & Then
        mockMvc.perform(post("/profile/change-password")
                        .with(user("testUser").roles("USER"))
                        .with(csrf())
                        .param("currentPassword", "WrongPass")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("errorMessage", "Invalid current password"));
    }

    @Test
    @DisplayName("Should delete account and redirect to login")
    void shouldDeleteAccount() throws Exception {
        // Given
        User user = new User("userToDelete", "password");
        userRepository.save(user);

        // When & Then
        mockMvc.perform(post("/profile/delete")
                        .with(user("userToDelete").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?deleted"));
    }
}