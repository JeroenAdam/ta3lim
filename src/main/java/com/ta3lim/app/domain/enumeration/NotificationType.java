package com.ta3lim.app.domain.enumeration;

/**
 * The NotificationType enumeration.
 */
public enum NotificationType {
    UNREAD_MESSAGES("Unread messages"),
    ACCEPTED("Submission accepted"),
    REJECTED("Submission rejected"),
    NONE("None");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
