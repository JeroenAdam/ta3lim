package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.SubjectDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.Subject}.
 */
public interface SubjectService {
    /**
     * Save a subject.
     *
     * @param subjectDTO the entity to save.
     * @return the persisted entity.
     */
    SubjectDTO save(SubjectDTO subjectDTO);

    /**
     * Updates a subject.
     *
     * @param subjectDTO the entity to update.
     * @return the persisted entity.
     */
    SubjectDTO update(SubjectDTO subjectDTO);

    /**
     * Partially updates a subject.
     *
     * @param subjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubjectDTO> partialUpdate(SubjectDTO subjectDTO);

    /**
     * Get all the subjects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubjectDTO> findAll(Pageable pageable);

    /**
     * Get the "id" subject.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubjectDTO> findOne(Long id);

    /**
     * Delete the "id" subject.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the subject corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubjectDTO> search(String query, Pageable pageable);
}
