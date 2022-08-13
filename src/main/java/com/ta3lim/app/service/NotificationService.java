package com.ta3lim.app.service;

import com.ta3lim.app.service.dto.NotificationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.ta3lim.app.domain.Notification}.
 */
public interface NotificationService {
    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationDTO save(NotificationDTO notificationDTO);

    /**
     * Updates a notification.
     *
     * @param notificationDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationDTO update(NotificationDTO notificationDTO);

    /**
     * Partially updates a notification.
     *
     * @param notificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO);

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationDTO> findAll(Pageable pageable);

    /**
     * Get all the notifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" notification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationDTO> findOne(Long id);

    /**
     * Delete the "id" notification.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the notification corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationDTO> search(String query, Pageable pageable);
}
