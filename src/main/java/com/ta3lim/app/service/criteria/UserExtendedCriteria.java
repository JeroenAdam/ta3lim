package com.ta3lim.app.service.criteria;

import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.CivilStatus;
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
 * Criteria class for the {@link com.ta3lim.app.domain.UserExtended} entity. This class is used
 * in {@link com.ta3lim.app.web.rest.UserExtendedResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-extendeds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class UserExtendedCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CivilStatus
     */
    public static class CivilStatusFilter extends Filter<CivilStatus> {

        public CivilStatusFilter() {}

        public CivilStatusFilter(CivilStatusFilter filter) {
            super(filter);
        }

        @Override
        public CivilStatusFilter copy() {
            return new CivilStatusFilter(this);
        }
    }

    /**
     * Class for filtering Children
     */
    public static class ChildrenFilter extends Filter<Children> {

        public ChildrenFilter() {}

        public ChildrenFilter(ChildrenFilter filter) {
            super(filter);
        }

        @Override
        public ChildrenFilter copy() {
            return new ChildrenFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter lastLogin;

    private StringFilter aboutMe;

    private StringFilter occupation;

    private StringFilter socialMedia;

    private CivilStatusFilter civilStatus;

    private ChildrenFilter firstchild;

    private ChildrenFilter secondchild;

    private ChildrenFilter thirdchild;

    private ChildrenFilter fourthchild;

    private IntegerFilter filesquota;

    private LocalDateFilter approverSince;

    private LocalDateFilter lastApproval;

    private StringFilter userId;

    private Boolean distinct;

    public UserExtendedCriteria() {}

    public UserExtendedCriteria(UserExtendedCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.lastLogin = other.lastLogin == null ? null : other.lastLogin.copy();
        this.aboutMe = other.aboutMe == null ? null : other.aboutMe.copy();
        this.occupation = other.occupation == null ? null : other.occupation.copy();
        this.socialMedia = other.socialMedia == null ? null : other.socialMedia.copy();
        this.civilStatus = other.civilStatus == null ? null : other.civilStatus.copy();
        this.firstchild = other.firstchild == null ? null : other.firstchild.copy();
        this.secondchild = other.secondchild == null ? null : other.secondchild.copy();
        this.thirdchild = other.thirdchild == null ? null : other.thirdchild.copy();
        this.fourthchild = other.fourthchild == null ? null : other.fourthchild.copy();
        this.filesquota = other.filesquota == null ? null : other.filesquota.copy();
        this.approverSince = other.approverSince == null ? null : other.approverSince.copy();
        this.lastApproval = other.lastApproval == null ? null : other.lastApproval.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public UserExtendedCriteria copy() {
        return new UserExtendedCriteria(this);
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

    public LocalDateFilter getLastLogin() {
        return lastLogin;
    }

    public LocalDateFilter lastLogin() {
        if (lastLogin == null) {
            lastLogin = new LocalDateFilter();
        }
        return lastLogin;
    }

    public void setLastLogin(LocalDateFilter lastLogin) {
        this.lastLogin = lastLogin;
    }

    public StringFilter getAboutMe() {
        return aboutMe;
    }

    public StringFilter aboutMe() {
        if (aboutMe == null) {
            aboutMe = new StringFilter();
        }
        return aboutMe;
    }

    public void setAboutMe(StringFilter aboutMe) {
        this.aboutMe = aboutMe;
    }

    public StringFilter getOccupation() {
        return occupation;
    }

    public StringFilter occupation() {
        if (occupation == null) {
            occupation = new StringFilter();
        }
        return occupation;
    }

    public void setOccupation(StringFilter occupation) {
        this.occupation = occupation;
    }

    public StringFilter getSocialMedia() {
        return socialMedia;
    }

    public StringFilter socialMedia() {
        if (socialMedia == null) {
            socialMedia = new StringFilter();
        }
        return socialMedia;
    }

    public void setSocialMedia(StringFilter socialMedia) {
        this.socialMedia = socialMedia;
    }

    public CivilStatusFilter getCivilStatus() {
        return civilStatus;
    }

    public CivilStatusFilter civilStatus() {
        if (civilStatus == null) {
            civilStatus = new CivilStatusFilter();
        }
        return civilStatus;
    }

    public void setCivilStatus(CivilStatusFilter civilStatus) {
        this.civilStatus = civilStatus;
    }

    public ChildrenFilter getFirstchild() {
        return firstchild;
    }

    public ChildrenFilter firstchild() {
        if (firstchild == null) {
            firstchild = new ChildrenFilter();
        }
        return firstchild;
    }

    public void setFirstchild(ChildrenFilter firstchild) {
        this.firstchild = firstchild;
    }

    public ChildrenFilter getSecondchild() {
        return secondchild;
    }

    public ChildrenFilter secondchild() {
        if (secondchild == null) {
            secondchild = new ChildrenFilter();
        }
        return secondchild;
    }

    public void setSecondchild(ChildrenFilter secondchild) {
        this.secondchild = secondchild;
    }

    public ChildrenFilter getThirdchild() {
        return thirdchild;
    }

    public ChildrenFilter thirdchild() {
        if (thirdchild == null) {
            thirdchild = new ChildrenFilter();
        }
        return thirdchild;
    }

    public void setThirdchild(ChildrenFilter thirdchild) {
        this.thirdchild = thirdchild;
    }

    public ChildrenFilter getFourthchild() {
        return fourthchild;
    }

    public ChildrenFilter fourthchild() {
        if (fourthchild == null) {
            fourthchild = new ChildrenFilter();
        }
        return fourthchild;
    }

    public void setFourthchild(ChildrenFilter fourthchild) {
        this.fourthchild = fourthchild;
    }

    public IntegerFilter getFilesquota() {
        return filesquota;
    }

    public IntegerFilter filesquota() {
        if (filesquota == null) {
            filesquota = new IntegerFilter();
        }
        return filesquota;
    }

    public void setFilesquota(IntegerFilter filesquota) {
        this.filesquota = filesquota;
    }

    public LocalDateFilter getApproverSince() {
        return approverSince;
    }

    public LocalDateFilter approverSince() {
        if (approverSince == null) {
            approverSince = new LocalDateFilter();
        }
        return approverSince;
    }

    public void setApproverSince(LocalDateFilter approverSince) {
        this.approverSince = approverSince;
    }

    public LocalDateFilter getLastApproval() {
        return lastApproval;
    }

    public LocalDateFilter lastApproval() {
        if (lastApproval == null) {
            lastApproval = new LocalDateFilter();
        }
        return lastApproval;
    }

    public void setLastApproval(LocalDateFilter lastApproval) {
        this.lastApproval = lastApproval;
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
        final UserExtendedCriteria that = (UserExtendedCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lastLogin, that.lastLogin) &&
            Objects.equals(aboutMe, that.aboutMe) &&
            Objects.equals(occupation, that.occupation) &&
            Objects.equals(socialMedia, that.socialMedia) &&
            Objects.equals(civilStatus, that.civilStatus) &&
            Objects.equals(firstchild, that.firstchild) &&
            Objects.equals(secondchild, that.secondchild) &&
            Objects.equals(thirdchild, that.thirdchild) &&
            Objects.equals(fourthchild, that.fourthchild) &&
            Objects.equals(filesquota, that.filesquota) &&
            Objects.equals(approverSince, that.approverSince) &&
            Objects.equals(lastApproval, that.lastApproval) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            lastLogin,
            aboutMe,
            occupation,
            socialMedia,
            civilStatus,
            firstchild,
            secondchild,
            thirdchild,
            fourthchild,
            filesquota,
            approverSince,
            lastApproval,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtendedCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (lastLogin != null ? "lastLogin=" + lastLogin + ", " : "") +
            (aboutMe != null ? "aboutMe=" + aboutMe + ", " : "") +
            (occupation != null ? "occupation=" + occupation + ", " : "") +
            (socialMedia != null ? "socialMedia=" + socialMedia + ", " : "") +
            (civilStatus != null ? "civilStatus=" + civilStatus + ", " : "") +
            (firstchild != null ? "firstchild=" + firstchild + ", " : "") +
            (secondchild != null ? "secondchild=" + secondchild + ", " : "") +
            (thirdchild != null ? "thirdchild=" + thirdchild + ", " : "") +
            (fourthchild != null ? "fourthchild=" + fourthchild + ", " : "") +
            (filesquota != null ? "filesquota=" + filesquota + ", " : "") +
            (approverSince != null ? "approverSince=" + approverSince + ", " : "") +
            (lastApproval != null ? "lastApproval=" + lastApproval + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
