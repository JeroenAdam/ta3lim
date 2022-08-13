package com.ta3lim.app.repository;

import com.ta3lim.app.domain.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Resource entity.
 *
 * When extending this class, extend ResourceRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ResourceRepository
    extends ResourceRepositoryWithBagRelationships, JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {
    @Query("select resource from Resource resource where resource.user.login = ?#{principal.preferredUsername}")
    List<Resource> findByUserIsCurrentUser();

    default Optional<Resource> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Resource> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Resource> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct resource from Resource resource left join fetch resource.user left join fetch resource.subject",
        countQuery = "select count(distinct resource) from Resource resource"
    )
    Page<Resource> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct resource from Resource resource left join fetch resource.user left join fetch resource.subject")
    List<Resource> findAllWithToOneRelationships();

    @Query("select resource from Resource resource left join fetch resource.user left join fetch resource.subject where resource.id =:id")
    Optional<Resource> findOneWithToOneRelationships(@Param("id") Long id);
}
