package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.SkillDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.Skill}.
 */
public interface SkillService {
    /**
     * Save a skill.
     *
     * @param skillDTO the entity to save.
     * @return the persisted entity.
     */
    SkillDTO save(SkillDTO skillDTO);

    /**
     * Updates a skill.
     *
     * @param skillDTO the entity to update.
     * @return the persisted entity.
     */
    SkillDTO update(SkillDTO skillDTO);

    /**
     * Partially updates a skill.
     *
     * @param skillDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SkillDTO> partialUpdate(SkillDTO skillDTO);

    /**
     * Get all the skills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SkillDTO> findAll(Pageable pageable);

    /**
     * Get the "id" skill.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SkillDTO> findOne(Long id);

    /**
     * Delete the "id" skill.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the skill corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SkillDTO> search(String query, Pageable pageable);
}
