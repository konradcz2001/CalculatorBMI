package com.github.konradcz2001.bmimpact.controller;

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
class BmiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should load home page successfully")
    void shouldLoadHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("bmiForm"));
    }

    @Test
    @DisplayName("Should calculate BMI and redirect with flash attributes")
    void shouldCalculateBmi() throws Exception {
        // Controller redirects (302) after calculation - correct behavior
        mockMvc.perform(post("/calculate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("height", "180")
                        .param("weight", "80")
                        .param("unitSystem", "METRIC"))
                .andExpect(status().is3xxRedirection()) // Expect 302
                .andExpect(redirectedUrl("/"))          // Back to home page
                .andExpect(flash().attributeExists("calculatedResult")); // Result in FlashMap
    }

    @Test
    @DisplayName("Should fail validation when inputs are invalid")
    void shouldFailValidation() throws Exception {
        mockMvc.perform(post("/calculate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("height", "-10")
                        .param("weight", "80")
                        .param("unitSystem", "METRIC"))
                .andExpect(status().isOk()) // Returns same view with errors (200 OK)
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Should redirect unauthenticated user trying to delete history")
    void shouldRedirectUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/history/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login")); // Exact match for login URL
    }
}