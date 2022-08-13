package com.ta3lim.app.domain.enumeration;

/**
 * The CivilStatus enumeration.
 */
public enum CivilStatus {
    MARRIED("Married"),
    DIVORCED("Divorced"),
    WIDOWER("Widower"),
    SEPARATED("Separated"),
    SINGLE("Single"),
    ENGAGED("Engaged"),
    OTHER("Other");

    private final String value;

    CivilStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
