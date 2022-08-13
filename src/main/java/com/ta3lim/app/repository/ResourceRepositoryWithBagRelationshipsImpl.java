package com.ta3lim.app.repository;

import com.ta3lim.app.domain.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ResourceRepositoryWithBagRelationshipsImpl implements ResourceRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Resource> fetchBagRelationships(Optional<Resource> resource) {
        return resource.map(this::fetchTopics).map(this::fetchSkills);
    }

    @Override
    public Page<Resource> fetchBagRelationships(Page<Resource> resources) {
        return new PageImpl<>(fetchBagRelationships(resources.getContent()), resources.getPageable(), resources.getTotalElements());
    }

    @Override
    public List<Resource> fetchBagRelationships(List<Resource> resources) {
        return Optional.of(resources).map(this::fetchTopics).map(this::fetchSkills).orElse(Collections.emptyList());
    }

    Resource fetchTopics(Resource result) {
        return entityManager
            .createQuery(
                "select resource from Resource resource left join fetch resource.topics where resource is :resource",
                Resource.class
            )
            .setParameter("resource", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Resource> fetchTopics(List<Resource> resources) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, resources.size()).forEach(index -> order.put(resources.get(index).getId(), index));
        List<Resource> result = entityManager
            .createQuery(
                "select distinct resource from Resource resource left join fetch resource.topics where resource in :resources",
                Resource.class
            )
            .setParameter("resources", resources)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Resource fetchSkills(Resource result) {
        return entityManager
            .createQuery(
                "select resource from Resource resource left join fetch resource.skills where resource is :resource",
                Resource.class
            )
            .setParameter("resource", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Resource> fetchSkills(List<Resource> resources) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, resources.size()).forEach(index -> order.put(resources.get(index).getId(), index));
        List<Resource> result = entityManager
            .createQuery(
                "select distinct resource from Resource resource left join fetch resource.skills where resource in :resources",
                Resource.class
            )
            .setParameter("resources", resources)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
