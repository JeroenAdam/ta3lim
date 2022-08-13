package com.ta3lim.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.repository.SubjectRepository;
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
 * Spring Data Elasticsearch repository for the {@link Subject} entity.
 */
public interface SubjectSearchRepository extends ElasticsearchRepository<Subject, Long>, SubjectSearchRepositoryInternal {}

interface SubjectSearchRepositoryInternal {
    Page<Subject> search(String query, Pageable pageable);

    Page<Subject> search(Query query);

    void index(Subject entity);
}

class SubjectSearchRepositoryInternalImpl implements SubjectSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SubjectRepository repository;

    SubjectSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SubjectRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Subject> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Subject> search(Query query) {
        SearchHits<Subject> searchHits = elasticsearchTemplate.search(query, Subject.class);
        List<Subject> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Subject entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
