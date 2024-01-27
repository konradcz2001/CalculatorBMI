package com.konrad.kalkulatorbmi;// BmiForm.java

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BmiForm {

    @NotBlank(message = "Nazwa jest wymagana")
    private String name;

    @NotNull(message = "Wzrost jest wymagany")
    @Min(value = 50, message = "Wzrost minimalny - 50 cm")
    @Max(value = 300, message = "Wzrost maksymalny - 300 cm")
    private int height;

    @NotNull(message = "Waga jest wymagana")
    @Min(value = 0, message = "Waga minimalna - 0 kg")
    @Max(value = 500, message = "Waga maksymalna - 500 kg")
    private int weight;

    // getters and setters

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
}
