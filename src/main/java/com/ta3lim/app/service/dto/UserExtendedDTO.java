package com.ta3lim.app.service.dto;

import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.CivilStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ta3lim.app.domain.UserExtended} entity.
 */
public class UserExtendedDTO implements Serializable {

    private Long id;

    private LocalDate lastLogin;

    private String aboutMe;

    private String occupation;

    private String socialMedia;

    private CivilStatus civilStatus;

    private Children firstchild;

    private Children secondchild;

    private Children thirdchild;

    private Children fourthchild;

    private Integer filesquota;

    private LocalDate approverSince;

    private LocalDate lastApproval;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public CivilStatus getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(CivilStatus civilStatus) {
        this.civilStatus = civilStatus;
    }

    public Children getFirstchild() {
        return firstchild;
    }

    public void setFirstchild(Children firstchild) {
        this.firstchild = firstchild;
    }

    public Children getSecondchild() {
        return secondchild;
    }

    public void setSecondchild(Children secondchild) {
        this.secondchild = secondchild;
    }

    public Children getThirdchild() {
        return thirdchild;
    }

    public void setThirdchild(Children thirdchild) {
        this.thirdchild = thirdchild;
    }

    public Children getFourthchild() {
        return fourthchild;
    }

    public void setFourthchild(Children fourthchild) {
        this.fourthchild = fourthchild;
    }

    public Integer getFilesquota() {
        return filesquota;
    }

    public void setFilesquota(Integer filesquota) {
        this.filesquota = filesquota;
    }

    public LocalDate getApproverSince() {
        return approverSince;
    }

    public void setApproverSince(LocalDate approverSince) {
        this.approverSince = approverSince;
    }

    public LocalDate getLastApproval() {
        return lastApproval;
    }

    public void setLastApproval(LocalDate lastApproval) {
        this.lastApproval = lastApproval;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserExtendedDTO)) {
            return false;
        }

        UserExtendedDTO userExtendedDTO = (UserExtendedDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userExtendedDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtendedDTO{" +
            "id=" + getId() +
            ", lastLogin='" + getLastLogin() + "'" +
            ", aboutMe='" + getAboutMe() + "'" +
            ", occupation='" + getOccupation() + "'" +
            ", socialMedia='" + getSocialMedia() + "'" +
            ", civilStatus='" + getCivilStatus() + "'" +
            ", firstchild='" + getFirstchild() + "'" +
            ", secondchild='" + getSecondchild() + "'" +
            ", thirdchild='" + getThirdchild() + "'" +
            ", fourthchild='" + getFourthchild() + "'" +
            ", filesquota=" + getFilesquota() +
            ", approverSince='" + getApproverSince() + "'" +
            ", lastApproval='" + getLastApproval() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
