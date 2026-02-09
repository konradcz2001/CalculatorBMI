package com.github.konradcz2001.bmimpact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration form.
 */
public class UserRegistrationDto {

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 20, message = "{validation.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "{validation.username.pattern}")
    private String username;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, message = "{validation.password.size}")
    // Regex: Min 1 cyfra, 1 mała litera, 1 duża litera, 1 znak specjalny, brak białych znaków
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,}$", message = "{validation.password.pattern}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}