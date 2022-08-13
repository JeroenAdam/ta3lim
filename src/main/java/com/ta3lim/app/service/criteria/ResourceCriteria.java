package com.ta3lim.app.service.criteria;

import com.ta3lim.app.domain.enumeration.AgeRange;
import com.ta3lim.app.domain.enumeration.ResourceType;
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
 * Criteria class for the {@link com.ta3lim.app.domain.Resource} entity. This class is used
 * in {@link com.ta3lim.app.web.rest.ResourceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /resources?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ResourceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ResourceType
     */
    public static class ResourceTypeFilter extends Filter<ResourceType> {

        public ResourceTypeFilter() {}

        public ResourceTypeFilter(ResourceTypeFilter filter) {
            super(filter);
        }

        @Override
        public ResourceTypeFilter copy() {
            return new ResourceTypeFilter(this);
        }
    }

    /**
     * Class for filtering AgeRange
     */
    public static class AgeRangeFilter extends Filter<AgeRange> {

        public AgeRangeFilter() {}

        public AgeRangeFilter(AgeRangeFilter filter) {
            super(filter);
        }

        @Override
        public AgeRangeFilter copy() {
            return new AgeRangeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private LocalDateFilter creationDate;

    private StringFilter description;

    private ResourceTypeFilter resourceType;

    private AgeRangeFilter angeRage;

    private StringFilter url;

    private StringFilter author;

    private LocalDateFilter lastUpdated;

    private BooleanFilter activated;

    private LongFilter views;

    private LongFilter votes;

    private StringFilter approvedBy;

    private StringFilter userId;

    private LongFilter subjectId;

    private LongFilter topicsId;

    private LongFilter skillsId;

    private Boolean distinct;

    public ResourceCriteria() {}

    public ResourceCriteria(ResourceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.creationDate = other.creationDate == null ? null : other.creationDate.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.resourceType = other.resourceType == null ? null : other.resourceType.copy();
        this.angeRage = other.angeRage == null ? null : other.angeRage.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.lastUpdated = other.lastUpdated == null ? null : other.lastUpdated.copy();
        this.activated = other.activated == null ? null : other.activated.copy();
        this.views = other.views == null ? null : other.views.copy();
        this.votes = other.votes == null ? null : other.votes.copy();
        this.approvedBy = other.approvedBy == null ? null : other.approvedBy.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.subjectId = other.subjectId == null ? null : other.subjectId.copy();
        this.topicsId = other.topicsId == null ? null : other.topicsId.copy();
        this.skillsId = other.skillsId == null ? null : other.skillsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ResourceCriteria copy() {
        return new ResourceCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
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

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ResourceTypeFilter getResourceType() {
        return resourceType;
    }

    public ResourceTypeFilter resourceType() {
        if (resourceType == null) {
            resourceType = new ResourceTypeFilter();
        }
        return resourceType;
    }

    public void setResourceType(ResourceTypeFilter resourceType) {
        this.resourceType = resourceType;
    }

    public AgeRangeFilter getAngeRage() {
        return angeRage;
    }

    public AgeRangeFilter angeRage() {
        if (angeRage == null) {
            angeRage = new AgeRangeFilter();
        }
        return angeRage;
    }

    public void setAngeRage(AgeRangeFilter angeRage) {
        this.angeRage = angeRage;
    }

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getAuthor() {
        return author;
    }

    public StringFilter author() {
        if (author == null) {
            author = new StringFilter();
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public LocalDateFilter getLastUpdated() {
        return lastUpdated;
    }

    public LocalDateFilter lastUpdated() {
        if (lastUpdated == null) {
            lastUpdated = new LocalDateFilter();
        }
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateFilter lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public BooleanFilter getActivated() {
        return activated;
    }

    public BooleanFilter activated() {
        if (activated == null) {
            activated = new BooleanFilter();
        }
        return activated;
    }

    public void setActivated(BooleanFilter activated) {
        this.activated = activated;
    }

    public LongFilter getViews() {
        return views;
    }

    public LongFilter views() {
        if (views == null) {
            views = new LongFilter();
        }
        return views;
    }

    public void setViews(LongFilter views) {
        this.views = views;
    }

    public LongFilter getVotes() {
        return votes;
    }

    public LongFilter votes() {
        if (votes == null) {
            votes = new LongFilter();
        }
        return votes;
    }

    public void setVotes(LongFilter votes) {
        this.votes = votes;
    }

    public StringFilter getApprovedBy() {
        return approvedBy;
    }

    public StringFilter approvedBy() {
        if (approvedBy == null) {
            approvedBy = new StringFilter();
        }
        return approvedBy;
    }

    public void setApprovedBy(StringFilter approvedBy) {
        this.approvedBy = approvedBy;
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

    public LongFilter getSubjectId() {
        return subjectId;
    }

    public LongFilter subjectId() {
        if (subjectId == null) {
            subjectId = new LongFilter();
        }
        return subjectId;
    }

    public void setSubjectId(LongFilter subjectId) {
        this.subjectId = subjectId;
    }

    public LongFilter getTopicsId() {
        return topicsId;
    }

    public LongFilter topicsId() {
        if (topicsId == null) {
            topicsId = new LongFilter();
        }
        return topicsId;
    }

    public void setTopicsId(LongFilter topicsId) {
        this.topicsId = topicsId;
    }

    public LongFilter getSkillsId() {
        return skillsId;
    }

    public LongFilter skillsId() {
        if (skillsId == null) {
            skillsId = new LongFilter();
        }
        return skillsId;
    }

    public void setSkillsId(LongFilter skillsId) {
        this.skillsId = skillsId;
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
        final ResourceCriteria that = (ResourceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(description, that.description) &&
            Objects.equals(resourceType, that.resourceType) &&
            Objects.equals(angeRage, that.angeRage) &&
            Objects.equals(url, that.url) &&
            Objects.equals(author, that.author) &&
            Objects.equals(lastUpdated, that.lastUpdated) &&
            Objects.equals(activated, that.activated) &&
            Objects.equals(views, that.views) &&
            Objects.equals(votes, that.votes) &&
            Objects.equals(approvedBy, that.approvedBy) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(subjectId, that.subjectId) &&
            Objects.equals(topicsId, that.topicsId) &&
            Objects.equals(skillsId, that.skillsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            creationDate,
            description,
            resourceType,
            angeRage,
            url,
            author,
            lastUpdated,
            activated,
            views,
            votes,
            approvedBy,
            userId,
            subjectId,
            topicsId,
            skillsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (resourceType != null ? "resourceType=" + resourceType + ", " : "") +
            (angeRage != null ? "angeRage=" + angeRage + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (author != null ? "author=" + author + ", " : "") +
            (lastUpdated != null ? "lastUpdated=" + lastUpdated + ", " : "") +
            (activated != null ? "activated=" + activated + ", " : "") +
            (views != null ? "views=" + views + ", " : "") +
            (votes != null ? "votes=" + votes + ", " : "") +
            (approvedBy != null ? "approvedBy=" + approvedBy + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (subjectId != null ? "subjectId=" + subjectId + ", " : "") +
            (topicsId != null ? "topicsId=" + topicsId + ", " : "") +
            (skillsId != null ? "skillsId=" + skillsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
