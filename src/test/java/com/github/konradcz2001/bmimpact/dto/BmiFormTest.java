package com.github.konradcz2001.bmimpact.dto;

import com.github.konradcz2001.bmimpact.model.UnitSystem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BmiFormTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation for valid metric data")
    void shouldPassValidation() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(180);
        form.setWeight(80);
        form.setUnitSystem(UnitSystem.METRIC);

        // When
        Set<ConstraintViolation<BmiForm>> violations = validator.validate(form);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when height is too low or too high")
    void shouldFailInvalidHeight() {
        // Given
        BmiForm formLow = new BmiForm();
        formLow.setHeight(5); // Min is 10
        formLow.setWeight(80);

        BmiForm formHigh = new BmiForm();
        formHigh.setHeight(350); // Max is 300
        formHigh.setWeight(80);

        // When
        Set<ConstraintViolation<BmiForm>> violationsLow = validator.validate(formLow);
        Set<ConstraintViolation<BmiForm>> violationsHigh = validator.validate(formHigh);

        // Then
        assertThat(violationsLow).isNotEmpty()
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.height.min}");

        assertThat(violationsHigh).isNotEmpty()
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.height.max}");
    }

    @Test
    @DisplayName("Should fail when weight is invalid")
    void shouldFailInvalidWeight() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(180);
        form.setWeight(0); // Min is 1

        // When
        Set<ConstraintViolation<BmiForm>> violations = validator.validate(form);

        // Then
        assertThat(violations).isNotEmpty()
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.weight.min}");
    }

    @Test
    @DisplayName("Should fail when UnitSystem is null")
    void shouldFailNullUnitSystem() {
        // Given
        BmiForm form = new BmiForm();
        form.setHeight(180);
        form.setWeight(80);
        form.setUnitSystem(null);

        // When
        Set<ConstraintViolation<BmiForm>> violations = validator.validate(form);

        // Then
        assertThat(violations).isNotEmpty()
                .extracting(ConstraintViolation::getMessageTemplate)
                .contains("{validation.unit-system.required}");
    }
}