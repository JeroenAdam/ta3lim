package com.ta3lim.app.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "is_delivered")
    private Boolean isDelivered;

    @ManyToOne
    private User receiver;

    @ManyToOne
    private User sender;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public Message creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public Message messageText(String messageText) {
        this.setMessageText(messageText);
        return this;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Boolean getIsDelivered() {
        return this.isDelivered;
    }

    public Message isDelivered(Boolean isDelivered) {
        this.setIsDelivered(isDelivered);
        return this;
    }

    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public User getReceiver() {
        return this.receiver;
    }

    public void setReceiver(User user) {
        this.receiver = user;
    }

    public Message receiver(User user) {
        this.setReceiver(user);
        return this;
    }

    public User getSender() {
        return this.sender;
    }

    public void setSender(User user) {
        this.sender = user;
    }

    public Message sender(User user) {
        this.setSender(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", messageText='" + getMessageText() + "'" +
            ", isDelivered='" + getIsDelivered() + "'" +
            "}";
    }
}
