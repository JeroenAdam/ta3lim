package com.ta3lim.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.repository.FavoriteRepository;
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
 * Spring Data Elasticsearch repository for the {@link Favorite} entity.
 */
public interface FavoriteSearchRepository extends ElasticsearchRepository<Favorite, Long>, FavoriteSearchRepositoryInternal {}

interface FavoriteSearchRepositoryInternal {
    Page<Favorite> search(String query, Pageable pageable);

    Page<Favorite> search(Query query);

    void index(Favorite entity);
}

class FavoriteSearchRepositoryInternalImpl implements FavoriteSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FavoriteRepository repository;

    FavoriteSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, FavoriteRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Favorite> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Favorite> search(Query query) {
        SearchHits<Favorite> searchHits = elasticsearchTemplate.search(query, Favorite.class);
        List<Favorite> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Favorite entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
