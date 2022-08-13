package com.ta3lim.app.service.criteria;

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
 * Criteria class for the {@link com.ta3lim.app.domain.Message} entity. This class is used
 * in {@link com.ta3lim.app.web.rest.MessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class MessageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter creationDate;

    private StringFilter messageText;

    private BooleanFilter isDelivered;

    private StringFilter receiverId;

    private StringFilter senderId;

    private Boolean distinct;

    public MessageCriteria() {}

    public MessageCriteria(MessageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.creationDate = other.creationDate == null ? null : other.creationDate.copy();
        this.messageText = other.messageText == null ? null : other.messageText.copy();
        this.isDelivered = other.isDelivered == null ? null : other.isDelivered.copy();
        this.receiverId = other.receiverId == null ? null : other.receiverId.copy();
        this.senderId = other.senderId == null ? null : other.senderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MessageCriteria copy() {
        return new MessageCriteria(this);
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

    public StringFilter getMessageText() {
        return messageText;
    }

    public StringFilter messageText() {
        if (messageText == null) {
            messageText = new StringFilter();
        }
        return messageText;
    }

    public void setMessageText(StringFilter messageText) {
        this.messageText = messageText;
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

    public StringFilter getReceiverId() {
        return receiverId;
    }

    public StringFilter receiverId() {
        if (receiverId == null) {
            receiverId = new StringFilter();
        }
        return receiverId;
    }

    public void setReceiverId(StringFilter receiverId) {
        this.receiverId = receiverId;
    }

    public StringFilter getSenderId() {
        return senderId;
    }

    public StringFilter senderId() {
        if (senderId == null) {
            senderId = new StringFilter();
        }
        return senderId;
    }

    public void setSenderId(StringFilter senderId) {
        this.senderId = senderId;
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
        final MessageCriteria that = (MessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(messageText, that.messageText) &&
            Objects.equals(isDelivered, that.isDelivered) &&
            Objects.equals(receiverId, that.receiverId) &&
            Objects.equals(senderId, that.senderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, messageText, isDelivered, receiverId, senderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
            (messageText != null ? "messageText=" + messageText + ", " : "") +
            (isDelivered != null ? "isDelivered=" + isDelivered + ", " : "") +
            (receiverId != null ? "receiverId=" + receiverId + ", " : "") +
            (senderId != null ? "senderId=" + senderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
