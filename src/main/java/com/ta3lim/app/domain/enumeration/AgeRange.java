package com.ta3lim.app.domain.enumeration;

/**
 * The AgeRange enumeration.
 */
public enum AgeRange {
    AGE_ALL("all ages"),
    AGE_04_06("age 4-6"),
    AGE_07_09("age 7-9"),
    AGE_10_12("age 10-12"),
    AGE_13_15("age 13-15"),
    AGE_16_18("age 16-18");

    private final String value;

    AgeRange(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
