package com.ta3lim.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ta3lim.app.domain.Votes} entity.
 */
public class VotesDTO implements Serializable {

    private Long id;

    private UserDTO user;

    private ResourceDTO resource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(o instanceof VotesDTO)) {
            return false;
        }

        VotesDTO votesDTO = (VotesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, votesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VotesDTO{" +
            "id=" + getId() +
            ", user=" + getUser() +
            ", resource=" + getResource() +
            "}";
    }
}
