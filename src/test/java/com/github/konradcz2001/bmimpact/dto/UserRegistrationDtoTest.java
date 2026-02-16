package com.github.konradcz2001.bmimpact.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation for valid username and password")
    void shouldPassValidation() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("validUser");
        dto.setPassword("StrongPass1!");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"us", "toolongusernameforthelimit", "invalid char", "user@name"})
    @DisplayName("Should fail validation for invalid username formats")
    void shouldFailInvalidUsername(String invalidUsername) {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(invalidUsername);
        dto.setPassword("StrongPass1!");

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessageTemplate)
                .anyMatch(msg -> msg.contains("validation.username"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short1!",       // Too short
            "nodigits!",     // No digit
            "NoSpecialChar1",// No special char
            "alllower1!",    // No uppercase
            "ALLUPPER1!",    // No lowercase
            "With Space1!"   // Contains space
    })
    @DisplayName("Should fail validation for weak passwords")
    void shouldFailWeakPassword(String weakPassword) {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("validUser");
        dto.setPassword(weakPassword);

        // When
        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessageTemplate)
                .anyMatch(msg -> msg.contains("validation.password"));
    }
}