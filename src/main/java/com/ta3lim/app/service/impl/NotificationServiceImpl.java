package com.ta3lim.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.domain.Notification;
import com.ta3lim.app.repository.NotificationRepository;
import com.ta3lim.app.repository.search.NotificationSearchRepository;
import com.ta3lim.app.service.NotificationService;
import com.ta3lim.app.service.dto.NotificationDTO;
import com.ta3lim.app.service.mapper.NotificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSearchRepository notificationSearchRepository;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        NotificationSearchRepository notificationSearchRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationSearchRepository = notificationSearchRepository;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        NotificationDTO result = notificationMapper.toDto(notification);
        notificationSearchRepository.index(notification);
        return result;
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        NotificationDTO result = notificationMapper.toDto(notification);
        notificationSearchRepository.index(notification);
        return result;
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        log.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(savedNotification -> {
                notificationSearchRepository.save(savedNotification);

                return savedNotification;
            })
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    public Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return notificationRepository.findAllWithEagerRelationships(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findOneWithEagerRelationships(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
        notificationSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Notifications for query {}", query);
        return notificationSearchRepository.search(query, pageable).map(notificationMapper::toDto);
    }
}
