package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.repository.ResourceRepository;
import com.ta3lim.app.repository.search.ResourceSearchRepository;
import com.ta3lim.app.service.criteria.ResourceCriteria;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.mapper.ResourceMapper;
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
 * Service for executing complex queries for {@link Resource} entities in the database.
 * The main input is a {@link ResourceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ResourceDTO} or a {@link Page} of {@link ResourceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResourceQueryService extends QueryService<Resource> {

    private final Logger log = LoggerFactory.getLogger(ResourceQueryService.class);

    private final ResourceRepository resourceRepository;

    private final ResourceMapper resourceMapper;

    private final ResourceSearchRepository resourceSearchRepository;

    public ResourceQueryService(
        ResourceRepository resourceRepository,
        ResourceMapper resourceMapper,
        ResourceSearchRepository resourceSearchRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.resourceSearchRepository = resourceSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ResourceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ResourceDTO> findByCriteria(ResourceCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Resource> specification = createSpecification(criteria);
        return resourceMapper.toDto(resourceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ResourceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ResourceDTO> findByCriteria(ResourceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Resource> specification = createSpecification(criteria);
        return resourceRepository.findAll(specification, page).map(resourceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ResourceCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Resource> specification = createSpecification(criteria);
        return resourceRepository.count(specification);
    }

    /**
     * Function to convert {@link ResourceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Resource> createSpecification(ResourceCriteria criteria) {
        Specification<Resource> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Resource_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Resource_.title));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Resource_.creationDate));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Resource_.description));
            }
            if (criteria.getResourceType() != null) {
                specification = specification.and(buildSpecification(criteria.getResourceType(), Resource_.resourceType));
            }
            if (criteria.getAngeRage() != null) {
                specification = specification.and(buildSpecification(criteria.getAngeRage(), Resource_.angeRage));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Resource_.url));
            }
            if (criteria.getAuthor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuthor(), Resource_.author));
            }
            if (criteria.getLastUpdated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastUpdated(), Resource_.lastUpdated));
            }
            if (criteria.getActivated() != null) {
                specification = specification.and(buildSpecification(criteria.getActivated(), Resource_.activated));
            }
            if (criteria.getViews() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getViews(), Resource_.views));
            }
            if (criteria.getVotes() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVotes(), Resource_.votes));
            }
            if (criteria.getApprovedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getApprovedBy(), Resource_.approvedBy));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Resource_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getSubjectId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSubjectId(), root -> root.join(Resource_.subject, JoinType.LEFT).get(Subject_.id))
                    );
            }
            if (criteria.getTopicsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTopicsId(), root -> root.join(Resource_.topics, JoinType.LEFT).get(Topic_.id))
                    );
            }
            if (criteria.getSkillsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSkillsId(), root -> root.join(Resource_.skills, JoinType.LEFT).get(Skill_.id))
                    );
            }
        }
        return specification;
    }
}
