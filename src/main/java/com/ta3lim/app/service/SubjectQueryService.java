package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.repository.SubjectRepository;
import com.ta3lim.app.repository.search.SubjectSearchRepository;
import com.ta3lim.app.service.criteria.SubjectCriteria;
import com.ta3lim.app.service.dto.SubjectDTO;
import com.ta3lim.app.service.mapper.SubjectMapper;
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
 * Service for executing complex queries for {@link Subject} entities in the database.
 * The main input is a {@link SubjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SubjectDTO} or a {@link Page} of {@link SubjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SubjectQueryService extends QueryService<Subject> {

    private final Logger log = LoggerFactory.getLogger(SubjectQueryService.class);

    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    private final SubjectSearchRepository subjectSearchRepository;

    public SubjectQueryService(
        SubjectRepository subjectRepository,
        SubjectMapper subjectMapper,
        SubjectSearchRepository subjectSearchRepository
    ) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
        this.subjectSearchRepository = subjectSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SubjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByCriteria(SubjectCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Subject> specification = createSpecification(criteria);
        return subjectMapper.toDto(subjectRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SubjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByCriteria(SubjectCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Subject> specification = createSpecification(criteria);
        return subjectRepository.findAll(specification, page).map(subjectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SubjectCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Subject> specification = createSpecification(criteria);
        return subjectRepository.count(specification);
    }

    /**
     * Function to convert {@link SubjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Subject> createSpecification(SubjectCriteria criteria) {
        Specification<Subject> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Subject_.id));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Subject_.label));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Subject_.creationDate));
            }
        }
        return specification;
    }
}
