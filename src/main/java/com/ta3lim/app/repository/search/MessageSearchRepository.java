package com.ta3lim.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.ta3lim.app.domain.Message;
import com.ta3lim.app.repository.MessageRepository;
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
 * Spring Data Elasticsearch repository for the {@link Message} entity.
 */
public interface MessageSearchRepository extends ElasticsearchRepository<Message, Long>, MessageSearchRepositoryInternal {}

interface MessageSearchRepositoryInternal {
    Page<Message> search(String query, Pageable pageable);

    Page<Message> search(Query query);

    void index(Message entity);
}

class MessageSearchRepositoryInternalImpl implements MessageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final MessageRepository repository;

    MessageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, MessageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Message> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Message> search(Query query) {
        SearchHits<Message> searchHits = elasticsearchTemplate.search(query, Message.class);
        List<Message> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Message entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
