package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.FavoriteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.Favorite}.
 */
public interface FavoriteService {
    /**
     * Save a favorite.
     *
     * @param favoriteDTO the entity to save.
     * @return the persisted entity.
     */
    FavoriteDTO save(FavoriteDTO favoriteDTO);

    /**
     * Updates a favorite.
     *
     * @param favoriteDTO the entity to update.
     * @return the persisted entity.
     */
    FavoriteDTO update(FavoriteDTO favoriteDTO);

    /**
     * Partially updates a favorite.
     *
     * @param favoriteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO);

    /**
     * Get all the favorites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FavoriteDTO> findAll(Pageable pageable);

    /**
     * Get all the favorites with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FavoriteDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" favorite.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FavoriteDTO> findOne(Long id);

    /**
     * Delete the "id" favorite.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the favorite corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FavoriteDTO> search(String query, Pageable pageable);
}
