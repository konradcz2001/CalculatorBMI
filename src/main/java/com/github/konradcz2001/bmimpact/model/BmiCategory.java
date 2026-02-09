package com.github.konradcz2001.bmimpact.model;

/**
 * Enum representing BMI categories based on WHO standards.
 * Includes display names and CSS classes for UI styling.
 */
public enum BmiCategory {
    UNDERWEIGHT("Niedowaga", "bmi-underweight"),
    NORMAL("Norma", "bmi-normal"),
    OVERWEIGHT("Nadwaga", "bmi-overweight"),
    OBESE("Otyłość", "bmi-obese");

    private final String displayName;
    private final String cssClass;

    BmiCategory(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }
}