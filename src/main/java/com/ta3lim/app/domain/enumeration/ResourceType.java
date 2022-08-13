package com.ta3lim.app.domain.enumeration;

/**
 * The ResourceType enumeration.
 */
public enum ResourceType {
    ARTICLES("articles"),
    DOCUMENTS("documents"),
    PRESENTATION("presentations"),
    IMAGES("images"),
    URLS("url&#39;s"),
    ANNOUNCEMENTS("announcements"),
    OTHER("other");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
