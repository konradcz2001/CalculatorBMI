package com.github.konradcz2001.bmimpact.controller;

import com.github.konradcz2001.bmimpact.config.SecurityConfig;
import com.github.konradcz2001.bmimpact.model.BmiCategory;
import com.github.konradcz2001.bmimpact.model.BmiResult;
import com.github.konradcz2001.bmimpact.service.BmiService;
import com.github.konradcz2001.bmimpact.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BmiController.class)
@Import(SecurityConfig.class) // Crucial to resolve Authentication parameters in controller
class BmiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BmiService bmiService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService; // Required by SecurityConfig

    @Test
    @DisplayName("Should display index page for anonymous user")
    void shouldDisplayIndex() throws Exception {
        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("bmiForm"));

        verifyNoInteractions(bmiService);
    }

    @Test
    @DisplayName("Should process BMI calculation and redirect")
    void shouldCalculateBmi() throws Exception {
        // Given
        BmiResult mockResult = new BmiResult(null, 180, 75, 23.15, BmiCategory.NORMAL, new Date());
        when(bmiService.calculate(any())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/calculate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("height", "180")
                        .param("weight", "75")
                        .param("unitSystem", "METRIC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("calculatedResult", mockResult));

        verify(bmiService, times(1)).calculate(any());
        verify(bmiService, never()).saveResult(any(), any());
    }

    @Test
    @DisplayName("Should return to index on validation error")
    void shouldHandleValidationError() throws Exception {
        // When & Then
        mockMvc.perform(post("/calculate")
                        .with(csrf())
                        .param("height", "-1")
                        .param("weight", "75"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(bmiService);
    }
}