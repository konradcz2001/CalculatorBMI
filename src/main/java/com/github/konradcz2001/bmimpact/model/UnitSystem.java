package com.github.konradcz2001.bmimpact.model;

/**
 * Enum representing the available unit systems for input data.
 */
public enum UnitSystem {
    METRIC("Metryczny (kg, cm)"),
    IMPERIAL("Imperialny (lbs, in)");

    private final String displayValue;

    UnitSystem(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}