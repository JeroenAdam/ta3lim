package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.UserExtendedDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.UserExtended}.
 */
public interface UserExtendedService {
    /**
     * Save a userExtended.
     *
     * @param userExtendedDTO the entity to save.
     * @return the persisted entity.
     */
    UserExtendedDTO save(UserExtendedDTO userExtendedDTO);

    /**
     * Updates a userExtended.
     *
     * @param userExtendedDTO the entity to update.
     * @return the persisted entity.
     */
    UserExtendedDTO update(UserExtendedDTO userExtendedDTO);

    /**
     * Partially updates a userExtended.
     *
     * @param userExtendedDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserExtendedDTO> partialUpdate(UserExtendedDTO userExtendedDTO);

    /**
     * Get all the userExtendeds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserExtendedDTO> findAll(Pageable pageable);

    /**
     * Get all the userExtendeds with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserExtendedDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userExtended.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserExtendedDTO> findOne(Long id);

    /**
     * Delete the "id" userExtended.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the userExtended corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserExtendedDTO> search(String query, Pageable pageable);
}
