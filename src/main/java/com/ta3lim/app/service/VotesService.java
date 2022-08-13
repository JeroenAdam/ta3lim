package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.VotesDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.Votes}.
 */
public interface VotesService {
    /**
     * Save a votes.
     *
     * @param votesDTO the entity to save.
     * @return the persisted entity.
     */
    VotesDTO save(VotesDTO votesDTO);

    /**
     * Updates a votes.
     *
     * @param votesDTO the entity to update.
     * @return the persisted entity.
     */
    VotesDTO update(VotesDTO votesDTO);

    /**
     * Partially updates a votes.
     *
     * @param votesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VotesDTO> partialUpdate(VotesDTO votesDTO);

    /**
     * Get all the votes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VotesDTO> findAll(Pageable pageable);

    /**
     * Get all the votes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VotesDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" votes.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VotesDTO> findOne(Long id);

    /**
     * Delete the "id" votes.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the votes corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VotesDTO> search(String query, Pageable pageable);
}
