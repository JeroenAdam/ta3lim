package com.ta3lim.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Skill.
 */
@Entity
@Table(name = "skill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "skill")
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "skills")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "subject", "topics", "skills" }, allowSetters = true)
    private Set<Resource> resources = new HashSet<>();

    public Skill() {}

    public Skill(String label, LocalDate creationDate) {
        this.label = label;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return this.id;
    }

    public Skill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Skill label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public Skill creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Resource> getResources() {
        return this.resources;
    }

    public void setResources(Set<Resource> resources) {
        if (this.resources != null) {
            this.resources.forEach(i -> i.removeSkills(this));
        }
        if (resources != null) {
            resources.forEach(i -> i.addSkills(this));
        }
        this.resources = resources;
    }

    public Skill resources(Set<Resource> resources) {
        this.setResources(resources);
        return this;
    }

    public Skill addResource(Resource resource) {
        this.resources.add(resource);
        resource.getSkills().add(this);
        return this;
    }

    public Skill removeResource(Resource resource) {
        this.resources.remove(resource);
        resource.getSkills().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        return id != null && id.equals(((Skill) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Skill{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
