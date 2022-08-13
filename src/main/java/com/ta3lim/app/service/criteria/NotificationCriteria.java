package com.ta3lim.app.service.criteria;

import com.ta3lim.app.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.ta3lim.app.domain.Notification} entity. This class is used
 * in {@link com.ta3lim.app.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class NotificationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering NotificationType
     */
    public static class NotificationTypeFilter extends Filter<NotificationType> {

        public NotificationTypeFilter() {}

        public NotificationTypeFilter(NotificationTypeFilter filter) {
            super(filter);
        }

        @Override
        public NotificationTypeFilter copy() {
            return new NotificationTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter creationDate;

    private LocalDateFilter notificationDate;

    private NotificationTypeFilter notificationType;

    private StringFilter notificationText;

    private BooleanFilter isDelivered;

    private BooleanFilter isDeleted;

    private StringFilter userId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.creationDate = other.creationDate == null ? null : other.creationDate.copy();
        this.notificationDate = other.notificationDate == null ? null : other.notificationDate.copy();
        this.notificationType = other.notificationType == null ? null : other.notificationType.copy();
        this.notificationText = other.notificationText == null ? null : other.notificationText.copy();
        this.isDelivered = other.isDelivered == null ? null : other.isDelivered.copy();
        this.isDeleted = other.isDeleted == null ? null : other.isDeleted.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getCreationDate() {
        return creationDate;
    }

    public LocalDateFilter creationDate() {
        if (creationDate == null) {
            creationDate = new LocalDateFilter();
        }
        return creationDate;
    }

    public void setCreationDate(LocalDateFilter creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateFilter getNotificationDate() {
        return notificationDate;
    }

    public LocalDateFilter notificationDate() {
        if (notificationDate == null) {
            notificationDate = new LocalDateFilter();
        }
        return notificationDate;
    }

    public void setNotificationDate(LocalDateFilter notificationDate) {
        this.notificationDate = notificationDate;
    }

    public NotificationTypeFilter getNotificationType() {
        return notificationType;
    }

    public NotificationTypeFilter notificationType() {
        if (notificationType == null) {
            notificationType = new NotificationTypeFilter();
        }
        return notificationType;
    }

    public void setNotificationType(NotificationTypeFilter notificationType) {
        this.notificationType = notificationType;
    }

    public StringFilter getNotificationText() {
        return notificationText;
    }

    public StringFilter notificationText() {
        if (notificationText == null) {
            notificationText = new StringFilter();
        }
        return notificationText;
    }

    public void setNotificationText(StringFilter notificationText) {
        this.notificationText = notificationText;
    }

    public BooleanFilter getIsDelivered() {
        return isDelivered;
    }

    public BooleanFilter isDelivered() {
        if (isDelivered == null) {
            isDelivered = new BooleanFilter();
        }
        return isDelivered;
    }

    public void setIsDelivered(BooleanFilter isDelivered) {
        this.isDelivered = isDelivered;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            isDeleted = new BooleanFilter();
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(notificationDate, that.notificationDate) &&
            Objects.equals(notificationType, that.notificationType) &&
            Objects.equals(notificationText, that.notificationText) &&
            Objects.equals(isDelivered, that.isDelivered) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            creationDate,
            notificationDate,
            notificationType,
            notificationText,
            isDelivered,
            isDeleted,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
            (notificationDate != null ? "notificationDate=" + notificationDate + ", " : "") +
            (notificationType != null ? "notificationType=" + notificationType + ", " : "") +
            (notificationText != null ? "notificationText=" + notificationText + ", " : "") +
            (isDelivered != null ? "isDelivered=" + isDelivered + ", " : "") +
            (isDeleted != null ? "isDeleted=" + isDeleted + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
