package com.ta3lim.app.service;

import com.ta3lim.app.domain.*; // for static metamodels
import com.ta3lim.app.domain.UserExtended;
import com.ta3lim.app.repository.UserExtendedRepository;
import com.ta3lim.app.repository.search.UserExtendedSearchRepository;
import com.ta3lim.app.service.criteria.UserExtendedCriteria;
import com.ta3lim.app.service.dto.UserExtendedDTO;
import com.ta3lim.app.service.mapper.UserExtendedMapper;
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
 * Service for executing complex queries for {@link UserExtended} entities in the database.
 * The main input is a {@link UserExtendedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserExtendedDTO} or a {@link Page} of {@link UserExtendedDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserExtendedQueryService extends QueryService<UserExtended> {

    private final Logger log = LoggerFactory.getLogger(UserExtendedQueryService.class);

    private final UserExtendedRepository userExtendedRepository;

    private final UserExtendedMapper userExtendedMapper;

    private final UserExtendedSearchRepository userExtendedSearchRepository;

    public UserExtendedQueryService(
        UserExtendedRepository userExtendedRepository,
        UserExtendedMapper userExtendedMapper,
        UserExtendedSearchRepository userExtendedSearchRepository
    ) {
        this.userExtendedRepository = userExtendedRepository;
        this.userExtendedMapper = userExtendedMapper;
        this.userExtendedSearchRepository = userExtendedSearchRepository;
    }

    /**
     * Return a {@link List} of {@link UserExtendedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserExtendedDTO> findByCriteria(UserExtendedCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<UserExtended> specification = createSpecification(criteria);
        return userExtendedMapper.toDto(userExtendedRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserExtendedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserExtendedDTO> findByCriteria(UserExtendedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<UserExtended> specification = createSpecification(criteria);
        return userExtendedRepository.findAll(specification, page).map(userExtendedMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserExtendedCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<UserExtended> specification = createSpecification(criteria);
        return userExtendedRepository.count(specification);
    }

    /**
     * Function to convert {@link UserExtendedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserExtended> createSpecification(UserExtendedCriteria criteria) {
        Specification<UserExtended> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserExtended_.id));
            }
            if (criteria.getLastLogin() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastLogin(), UserExtended_.lastLogin));
            }
            if (criteria.getAboutMe() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAboutMe(), UserExtended_.aboutMe));
            }
            if (criteria.getOccupation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupation(), UserExtended_.occupation));
            }
            if (criteria.getSocialMedia() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSocialMedia(), UserExtended_.socialMedia));
            }
            if (criteria.getCivilStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getCivilStatus(), UserExtended_.civilStatus));
            }
            if (criteria.getFirstchild() != null) {
                specification = specification.and(buildSpecification(criteria.getFirstchild(), UserExtended_.firstchild));
            }
            if (criteria.getSecondchild() != null) {
                specification = specification.and(buildSpecification(criteria.getSecondchild(), UserExtended_.secondchild));
            }
            if (criteria.getThirdchild() != null) {
                specification = specification.and(buildSpecification(criteria.getThirdchild(), UserExtended_.thirdchild));
            }
            if (criteria.getFourthchild() != null) {
                specification = specification.and(buildSpecification(criteria.getFourthchild(), UserExtended_.fourthchild));
            }
            if (criteria.getFilesquota() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFilesquota(), UserExtended_.filesquota));
            }
            if (criteria.getApproverSince() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getApproverSince(), UserExtended_.approverSince));
            }
            if (criteria.getLastApproval() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastApproval(), UserExtended_.lastApproval));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(UserExtended_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
