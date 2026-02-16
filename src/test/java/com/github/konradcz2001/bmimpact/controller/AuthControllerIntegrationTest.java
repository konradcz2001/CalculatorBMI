package com.github.konradcz2001.bmimpact.controller;

import com.github.konradcz2001.bmimpact.model.User;
import com.github.konradcz2001.bmimpact.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should load login page")
    void shouldLoadLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Should load register page")
    void shouldLoadRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                // Correct attribute name is userDto based on controller logic
                .andExpect(model().attributeExists("userDto"));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "integrationUser")
                        .param("password", "StrongPass1!")
                        .param("confirmPassword", "StrongPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @DisplayName("Should fail registration if username is taken")
    void shouldFailIfUserExists() throws Exception {
        // Given: Existing user
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("password");
        userRepository.save(user);

        // When & Then
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "existingUser")
                        .param("password", "NewPass1!")
                        .param("confirmPassword", "NewPass1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                // Expect field errors on 'userDto' attribute
                .andExpect(model().attributeHasFieldErrors("userDto", "username"));
    }
}