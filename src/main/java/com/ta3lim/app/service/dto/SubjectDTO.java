package com.ta3lim.app.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ta3lim.app.domain.Subject} entity.
 */
public class SubjectDTO implements Serializable {

    private Long id;

    private String label;

    private LocalDate creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubjectDTO)) {
            return false;
        }

        SubjectDTO subjectDTO = (SubjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubjectDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
