package com.ta3lim.app.service.dto;

import com.ta3lim.app.domain.enumeration.AgeRange;
import com.ta3lim.app.domain.enumeration.ResourceType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ta3lim.app.domain.Resource} entity.
 */
public class ResourceDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private LocalDate creationDate;

    private String description;

    private ResourceType resourceType;

    private AgeRange angeRage;

    @Lob
    private byte[] file;

    private String fileContentType;
    private String url;

    private String author;

    private LocalDate lastUpdated;

    private Boolean activated;

    private Long views;

    private Long votes;

    private String approvedBy;

    private UserDTO user;

    private SubjectDTO subject;

    private Set<TopicDTO> topics = new HashSet<>();

    private Set<SkillDTO> skills = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public AgeRange getAngeRage() {
        return angeRage;
    }

    public void setAngeRage(AgeRange angeRage) {
        this.angeRage = angeRage;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    public Set<TopicDTO> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicDTO> topics) {
        this.topics = topics;
    }

    public Set<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillDTO> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceDTO)) {
            return false;
        }

        ResourceDTO resourceDTO = (ResourceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, resourceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", resourceType='" + getResourceType() + "'" +
            ", angeRage='" + getAngeRage() + "'" +
            ", file='" + getFile() + "'" +
            ", url='" + getUrl() + "'" +
            ", author='" + getAuthor() + "'" +
            ", lastUpdated='" + getLastUpdated() + "'" +
            ", activated='" + getActivated() + "'" +
            ", views=" + getViews() +
            ", votes=" + getVotes() +
            ", approvedBy='" + getApprovedBy() + "'" +
            ", user=" + getUser() +
            ", subject=" + getSubject() +
            ", topics=" + getTopics() +
            ", skills=" + getSkills() +
            "}";
    }
}
