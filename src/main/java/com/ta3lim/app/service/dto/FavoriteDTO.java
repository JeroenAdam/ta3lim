package com.ta3lim.app.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ta3lim.app.domain.Favorite} entity.
 */
public class FavoriteDTO implements Serializable {

    private Long id;

    private LocalDate creationDate;

    private UserDTO user;

    private ResourceDTO resource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FavoriteDTO)) {
            return false;
        }

        FavoriteDTO favoriteDTO = (FavoriteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, favoriteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FavoriteDTO{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", user=" + getUser() +
            ", resource=" + getResource() +
            "}";
    }
}
