package com.github.konradcz2001.bmimpact;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) backing the BMI calculation form.
 * Contains validation rules for user input compatible with both Metric and Imperial systems.
 */
public class BmiForm {

    @NotBlank(message = "Nazwa jest wymagana")
    private String name;

    // Adjusted constraints to fit both cm and inches (e.g., 10 inches is valid, 300 cm is valid)
    @NotNull(message = "Wzrost jest wymagany")
    @Min(value = 10, message = "Wartość zbyt mała (min. 10 cm/in)")
    @Max(value = 300, message = "Wartość zbyt duża (max. 300 cm/in)")
    private int height;

    // Adjusted constraints to fit both kg and lbs
    @NotNull(message = "Waga jest wymagana")
    @Min(value = 1, message = "Wartość zbyt mała")
    @Max(value = 1000, message = "Wartość zbyt duża")
    private int weight;

    @NotNull(message = "Wybór systemu miar jest wymagany")
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