package com.ta3lim.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ta3lim.app.domain.UserExtended;
import com.ta3lim.app.repository.UserExtendedRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link UserExtended} entity.
 */
public interface UserExtendedSearchRepository extends ElasticsearchRepository<UserExtended, Long>, UserExtendedSearchRepositoryInternal {}

interface UserExtendedSearchRepositoryInternal {
    Page<UserExtended> search(String query, Pageable pageable);

    Page<UserExtended> search(Query query);

    void index(UserExtended entity);
}

class UserExtendedSearchRepositoryInternalImpl implements UserExtendedSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final UserExtendedRepository repository;

    UserExtendedSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, UserExtendedRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserExtended> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<UserExtended> search(Query query) {
        SearchHits<UserExtended> searchHits = elasticsearchTemplate.search(query, UserExtended.class);
        List<UserExtended> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserExtended entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
