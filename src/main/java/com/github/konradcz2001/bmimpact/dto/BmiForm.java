package com.github.konradcz2001.bmimpact.dto;

import com.github.konradcz2001.bmimpact.model.UnitSystem;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) backing the BMI calculation form.
 * Contains validation rules for user input compatible with both Metric and Imperial systems.
 * validation messages use i18n keys.
 */
public class BmiForm {

    @NotBlank(message = "{validation.name.required}")
    private String name;

    // Adjusted constraints to fit both cm and inches
    @NotNull(message = "{validation.height.required}")
    @Min(value = 10, message = "{validation.height.min}")
    @Max(value = 300, message = "{validation.height.max}")
    private int height;

    // Adjusted constraints to fit both kg and lbs
    @NotNull(message = "{validation.weight.required}")
    @Min(value = 1, message = "{validation.weight.min}")
    @Max(value = 1000, message = "{validation.weight.max}")
    private int weight;

    @NotNull(message = "{validation.unit-system.required}")
    private UnitSystem unitSystem = UnitSystem.METRIC;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public UnitSystem getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(UnitSystem unitSystem) {
        this.unitSystem = unitSystem;
    }
}