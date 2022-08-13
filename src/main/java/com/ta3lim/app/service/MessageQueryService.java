package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.Message;
import com.ta3lim.app.repository.MessageRepository;
import com.ta3lim.app.repository.search.MessageSearchRepository;
import com.ta3lim.app.service.criteria.MessageCriteria;
import com.ta3lim.app.service.dto.MessageDTO;
import com.ta3lim.app.service.mapper.MessageMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Message} entities in the database.
 * The main input is a {@link MessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MessageDTO} or a {@link Page} of {@link MessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MessageQueryService extends QueryService<Message> {

    private final Logger log = LoggerFactory.getLogger(MessageQueryService.class);

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    private final MessageSearchRepository messageSearchRepository;

    public MessageQueryService(
        MessageRepository messageRepository,
        MessageMapper messageMapper,
        MessageSearchRepository messageSearchRepository
    ) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageSearchRepository = messageSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> findByCriteria(MessageCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Message> specification = createSpecification(criteria);
        return messageMapper.toDto(messageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MessageDTO> findByCriteria(MessageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Message> specification = createSpecification(criteria);
        return messageRepository.findAll(specification, page).map(messageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MessageCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Message> specification = createSpecification(criteria);
        return messageRepository.count(specification);
    }

    /**
     * Function to convert {@link MessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Message> createSpecification(MessageCriteria criteria) {
        Specification<Message> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Message_.id));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Message_.creationDate));
            }
            if (criteria.getMessageText() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessageText(), Message_.messageText));
            }
            if (criteria.getIsDelivered() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDelivered(), Message_.isDelivered));
            }
            if (criteria.getReceiverId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getReceiverId(), root -> root.join(Message_.receiver, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getSenderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSenderId(), root -> root.join(Message_.sender, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
