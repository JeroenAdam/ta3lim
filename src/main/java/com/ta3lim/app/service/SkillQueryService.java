package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.repository.SkillRepository;
import com.ta3lim.app.repository.search.SkillSearchRepository;
import com.ta3lim.app.service.criteria.SkillCriteria;
import com.ta3lim.app.service.dto.SkillDTO;
import com.ta3lim.app.service.mapper.SkillMapper;
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
 * Service for executing complex queries for {@link Skill} entities in the database.
 * The main input is a {@link SkillCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SkillDTO} or a {@link Page} of {@link SkillDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SkillQueryService extends QueryService<Skill> {

    private final Logger log = LoggerFactory.getLogger(SkillQueryService.class);

    private final SkillRepository skillRepository;

    private final SkillMapper skillMapper;

    private final SkillSearchRepository skillSearchRepository;

    public SkillQueryService(SkillRepository skillRepository, SkillMapper skillMapper, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillSearchRepository = skillSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SkillDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SkillDTO> findByCriteria(SkillCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Skill> specification = createSpecification(criteria);
        return skillMapper.toDto(skillRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SkillDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SkillDTO> findByCriteria(SkillCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Skill> specification = createSpecification(criteria);
        return skillRepository.findAll(specification, page).map(skillMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SkillCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Skill> specification = createSpecification(criteria);
        return skillRepository.count(specification);
    }

    /**
     * Function to convert {@link SkillCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Skill> createSpecification(SkillCriteria criteria) {
        Specification<Skill> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Skill_.id));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Skill_.label));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Skill_.creationDate));
            }
            if (criteria.getResourceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getResourceId(), root -> root.join(Skill_.resources, JoinType.LEFT).get(Resource_.id))
                    );
            }
        }
        return specification;
    }
}
