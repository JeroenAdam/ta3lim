package com.ta3lim.app.repository;

import com.ta3lim.app.domain.Votes;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Votes entity.
 */
@Repository
public interface VotesRepository extends JpaRepository<Votes, Long>, JpaSpecificationExecutor<Votes> {
    @Query("select votes from Votes votes where votes.user.login = ?#{principal.preferredUsername}")
    List<Votes> findByUserIsCurrentUser();

    default Optional<Votes> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Votes> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Votes> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct votes from Votes votes left join fetch votes.user",
        countQuery = "select count(distinct votes) from Votes votes"
    )
    Page<Votes> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct votes from Votes votes left join fetch votes.user")
    List<Votes> findAllWithToOneRelationships();

    @Query("select votes from Votes votes left join fetch votes.user where votes.id =:id")
    Optional<Votes> findOneWithToOneRelationships(@Param("id") Long id);
}
