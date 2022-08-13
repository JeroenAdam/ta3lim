package com.ta3lim.app.domain;

import com.ta3lim.app.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "notification_date")
    private LocalDate notificationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @Column(name = "notification_text")
    private String notificationText;

    @Column(name = "is_delivered")
    private Boolean isDelivered;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public Notification creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getNotificationDate() {
        return this.notificationDate;
    }

    public Notification notificationDate(LocalDate notificationDate) {
        this.setNotificationDate(notificationDate);
        return this;
    }

    public void setNotificationDate(LocalDate notificationDate) {
        this.notificationDate = notificationDate;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public Notification notificationType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationText() {
        return this.notificationText;
    }

    public Notification notificationText(String notificationText) {
        this.setNotificationText(notificationText);
        return this;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public Boolean getIsDelivered() {
        return this.isDelivered;
    }

    public Notification isDelivered(Boolean isDelivered) {
        this.setIsDelivered(isDelivered);
        return this;
    }

    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Notification isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", notificationDate='" + getNotificationDate() + "'" +
            ", notificationType='" + getNotificationType() + "'" +
            ", notificationText='" + getNotificationText() + "'" +
            ", isDelivered='" + getIsDelivered() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
