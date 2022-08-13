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
 * Criteria class for the {@link com.ta3lim.app.domain.Skill} entity. This class is used
 * in {@link com.ta3lim.app.web.rest.SkillResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /skills?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SkillCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter label;

    private LocalDateFilter creationDate;

    private LongFilter resourceId;

    private Boolean distinct;

    public SkillCriteria() {}

    public SkillCriteria(SkillCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.label = other.label == null ? null : other.label.copy();
        this.creationDate = other.creationDate == null ? null : other.creationDate.copy();
        this.resourceId = other.resourceId == null ? null : other.resourceId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SkillCriteria copy() {
        return new SkillCriteria(this);
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

    public StringFilter getLabel() {
        return label;
    }

    public StringFilter label() {
        if (label == null) {
            label = new StringFilter();
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
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

    public LongFilter getResourceId() {
        return resourceId;
    }

    public LongFilter resourceId() {
        if (resourceId == null) {
            resourceId = new LongFilter();
        }
        return resourceId;
    }

    public void setResourceId(LongFilter resourceId) {
        this.resourceId = resourceId;
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
        final SkillCriteria that = (SkillCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(label, that.label) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(resourceId, that.resourceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, creationDate, resourceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SkillCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (label != null ? "label=" + label + ", " : "") +
            (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
            (resourceId != null ? "resourceId=" + resourceId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
