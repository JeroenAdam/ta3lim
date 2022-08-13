package com.ta3lim.app.domain;

import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.CivilStatus;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserExtended.
 */
@Entity
@Table(name = "user_extended")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userextended")
public class UserExtended implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "last_login")
    private LocalDate lastLogin;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "social_media")
    private String socialMedia;

    @Enumerated(EnumType.STRING)
    @Column(name = "civil_status")
    private CivilStatus civilStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "firstchild")
    private Children firstchild;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondchild")
    private Children secondchild;

    @Enumerated(EnumType.STRING)
    @Column(name = "thirdchild")
    private Children thirdchild;

    @Enumerated(EnumType.STRING)
    @Column(name = "fourthchild")
    private Children fourthchild;

    @Column(name = "filesquota")
    private Integer filesquota;

    @Column(name = "approver_since")
    private LocalDate approverSince;

    @Column(name = "last_approval")
    private LocalDate lastApproval;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserExtended id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLastLogin() {
        return this.lastLogin;
    }

    public UserExtended lastLogin(LocalDate lastLogin) {
        this.setLastLogin(lastLogin);
        return this;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getAboutMe() {
        return this.aboutMe;
    }

    public UserExtended aboutMe(String aboutMe) {
        this.setAboutMe(aboutMe);
        return this;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getOccupation() {
        return this.occupation;
    }

    public UserExtended occupation(String occupation) {
        this.setOccupation(occupation);
        return this;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSocialMedia() {
        return this.socialMedia;
    }

    public UserExtended socialMedia(String socialMedia) {
        this.setSocialMedia(socialMedia);
        return this;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public CivilStatus getCivilStatus() {
        return this.civilStatus;
    }

    public UserExtended civilStatus(CivilStatus civilStatus) {
        this.setCivilStatus(civilStatus);
        return this;
    }

    public void setCivilStatus(CivilStatus civilStatus) {
        this.civilStatus = civilStatus;
    }

    public Children getFirstchild() {
        return this.firstchild;
    }

    public UserExtended firstchild(Children firstchild) {
        this.setFirstchild(firstchild);
        return this;
    }

    public void setFirstchild(Children firstchild) {
        this.firstchild = firstchild;
    }

    public Children getSecondchild() {
        return this.secondchild;
    }

    public UserExtended secondchild(Children secondchild) {
        this.setSecondchild(secondchild);
        return this;
    }

    public void setSecondchild(Children secondchild) {
        this.secondchild = secondchild;
    }

    public Children getThirdchild() {
        return this.thirdchild;
    }

    public UserExtended thirdchild(Children thirdchild) {
        this.setThirdchild(thirdchild);
        return this;
    }

    public void setThirdchild(Children thirdchild) {
        this.thirdchild = thirdchild;
    }

    public Children getFourthchild() {
        return this.fourthchild;
    }

    public UserExtended fourthchild(Children fourthchild) {
        this.setFourthchild(fourthchild);
        return this;
    }

    public void setFourthchild(Children fourthchild) {
        this.fourthchild = fourthchild;
    }

    public Integer getFilesquota() {
        return this.filesquota;
    }

    public UserExtended filesquota(Integer filesquota) {
        this.setFilesquota(filesquota);
        return this;
    }

    public void setFilesquota(Integer filesquota) {
        this.filesquota = filesquota;
    }

    public LocalDate getApproverSince() {
        return this.approverSince;
    }

    public UserExtended approverSince(LocalDate approverSince) {
        this.setApproverSince(approverSince);
        return this;
    }

    public void setApproverSince(LocalDate approverSince) {
        this.approverSince = approverSince;
    }

    public LocalDate getLastApproval() {
        return this.lastApproval;
    }

    public UserExtended lastApproval(LocalDate lastApproval) {
        this.setLastApproval(lastApproval);
        return this;
    }

    public void setLastApproval(LocalDate lastApproval) {
        this.lastApproval = lastApproval;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserExtended user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserExtended)) {
            return false;
        }
        return id != null && id.equals(((UserExtended) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtended{" +
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
            "}";
    }
}
