package com.ta3lim.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ta3lim.app.domain.enumeration.AgeRange;
import com.ta3lim.app.domain.enumeration.ResourceType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Resource.
 */
@Entity
@Table(name = "resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "resource")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Lob
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ange_rage")
    private AgeRange angeRage;

    @Lob
    @Column(name = "file")
    private byte[] file;

    @Column(name = "file_content_type")
    private String fileContentType;

    @Column(name = "url")
    private String url;

    @Column(name = "author")
    private String author;

    @Column(name = "last_updated")
    private LocalDate lastUpdated;

    @Column(name = "activated")
    private Boolean activated;

    @Column(name = "views")
    private Long views;

    @Column(name = "votes")
    private Long votes;

    @Column(name = "approved_by")
    private String approvedBy;

    @ManyToOne
    private User user;

    @ManyToOne
    private Subject subject;

    @ManyToMany
    @JoinTable(
        name = "rel_resource__topics",
        joinColumns = @JoinColumn(name = "resource_id"),
        inverseJoinColumns = @JoinColumn(name = "topics_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resources" }, allowSetters = true)
    private Set<Topic> topics = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_resource__skills",
        joinColumns = @JoinColumn(name = "resource_id"),
        inverseJoinColumns = @JoinColumn(name = "skills_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "resources" }, allowSetters = true)
    private Set<Skill> skills = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Resource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Resource title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public Resource creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return this.description;
    }

    public Resource description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public Resource resourceType(ResourceType resourceType) {
        this.setResourceType(resourceType);
        return this;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public AgeRange getAngeRage() {
        return this.angeRage;
    }

    public Resource angeRage(AgeRange angeRage) {
        this.setAngeRage(angeRage);
        return this;
    }

    public void setAngeRage(AgeRange angeRage) {
        this.angeRage = angeRage;
    }

    public byte[] getFile() {
        return this.file;
    }

    public Resource file(byte[] file) {
        this.setFile(file);
        return this;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return this.fileContentType;
    }

    public Resource fileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getUrl() {
        return this.url;
    }

    public Resource url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return this.author;
    }

    public Resource author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getLastUpdated() {
        return this.lastUpdated;
    }

    public Resource lastUpdated(LocalDate lastUpdated) {
        this.setLastUpdated(lastUpdated);
        return this;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getActivated() {
        return this.activated;
    }

    public Resource activated(Boolean activated) {
        this.setActivated(activated);
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Long getViews() {
        return this.views;
    }

    public Resource views(Long views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getVotes() {
        return this.votes;
    }

    public Resource votes(Long votes) {
        this.setVotes(votes);
        return this;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public String getApprovedBy() {
        return this.approvedBy;
    }

    public Resource approvedBy(String approvedBy) {
        this.setApprovedBy(approvedBy);
        return this;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource user(User user) {
        this.setUser(user);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Resource subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Set<Topic> getTopics() {
        return this.topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public Resource topics(Set<Topic> topics) {
        this.setTopics(topics);
        return this;
    }

    public Resource addTopics(Topic topic) {
        this.topics.add(topic);
        topic.getResources().add(this);
        return this;
    }

    public Resource removeTopics(Topic topic) {
        this.topics.remove(topic);
        topic.getResources().remove(this);
        return this;
    }

    public Set<Skill> getSkills() {
        return this.skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public Resource skills(Set<Skill> skills) {
        this.setSkills(skills);
        return this;
    }

    public Resource addSkills(Skill skill) {
        this.skills.add(skill);
        skill.getResources().add(this);
        return this;
    }

    public Resource removeSkills(Skill skill) {
        this.skills.remove(skill);
        skill.getResources().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        return id != null && id.equals(((Resource) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Resource{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", resourceType='" + getResourceType() + "'" +
            ", angeRage='" + getAngeRage() + "'" +
            ", file='" + getFile() + "'" +
            ", fileContentType='" + getFileContentType() + "'" +
            ", url='" + getUrl() + "'" +
            ", author='" + getAuthor() + "'" +
            ", lastUpdated='" + getLastUpdated() + "'" +
            ", activated='" + getActivated() + "'" +
            ", views=" + getViews() +
            ", votes=" + getVotes() +
            ", approvedBy='" + getApprovedBy() + "'" +
            "}";
    }
}
