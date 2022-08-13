package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.Votes;
import com.ta3lim.app.repository.VotesRepository;
import com.ta3lim.app.repository.search.VotesSearchRepository;
import com.ta3lim.app.service.criteria.VotesCriteria;
import com.ta3lim.app.service.dto.VotesDTO;
import com.ta3lim.app.service.mapper.VotesMapper;
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
 * Service for executing complex queries for {@link Votes} entities in the database.
 * The main input is a {@link VotesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VotesDTO} or a {@link Page} of {@link VotesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VotesQueryService extends QueryService<Votes> {

    private final Logger log = LoggerFactory.getLogger(VotesQueryService.class);

    private final VotesRepository votesRepository;

    private final VotesMapper votesMapper;

    private final VotesSearchRepository votesSearchRepository;

    public VotesQueryService(VotesRepository votesRepository, VotesMapper votesMapper, VotesSearchRepository votesSearchRepository) {
        this.votesRepository = votesRepository;
        this.votesMapper = votesMapper;
        this.votesSearchRepository = votesSearchRepository;
    }

    /**
     * Return a {@link List} of {@link VotesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VotesDTO> findByCriteria(VotesCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Votes> specification = createSpecification(criteria);
        return votesMapper.toDto(votesRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VotesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VotesDTO> findByCriteria(VotesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Votes> specification = createSpecification(criteria);
        return votesRepository.findAll(specification, page).map(votesMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VotesCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Votes> specification = createSpecification(criteria);
        return votesRepository.count(specification);
    }

    /**
     * Function to convert {@link VotesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Votes> createSpecification(VotesCriteria criteria) {
        Specification<Votes> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Votes_.id));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Votes_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getResourceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getResourceId(), root -> root.join(Votes_.resource, JoinType.LEFT).get(Resource_.id))
                    );
            }
        }
        return specification;
    }
}
