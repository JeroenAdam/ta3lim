package com.ta3lim.app.repository;

import com.ta3lim.app.domain.UserExtended;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserExtended entity.
 */
@Repository
public interface UserExtendedRepository extends JpaRepository<UserExtended, Long>, JpaSpecificationExecutor<UserExtended> {
    default Optional<UserExtended> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserExtended> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserExtended> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct userExtended from UserExtended userExtended left join fetch userExtended.user",
        countQuery = "select count(distinct userExtended) from UserExtended userExtended"
    )
    Page<UserExtended> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct userExtended from UserExtended userExtended left join fetch userExtended.user")
    List<UserExtended> findAllWithToOneRelationships();

    @Query("select userExtended from UserExtended userExtended left join fetch userExtended.user where userExtended.id =:id")
    Optional<UserExtended> findOneWithToOneRelationships(@Param("id") Long id);
}
