package com.ta3lim.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.repository.TopicRepository;
import com.ta3lim.app.service.TopicQueryService;
import com.ta3lim.app.service.TopicService;
import com.ta3lim.app.service.criteria.TopicCriteria;
import com.ta3lim.app.service.dto.TopicDTO;
import com.ta3lim.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ta3lim.app.domain.Topic}.
 */
@RestController
@RequestMapping("/api")
public class TopicResource {

    private final Logger log = LoggerFactory.getLogger(TopicResource.class);

    private static final String ENTITY_NAME = "topic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TopicService topicService;

    private final TopicRepository topicRepository;

    private final TopicQueryService topicQueryService;

    public TopicResource(TopicService topicService, TopicRepository topicRepository, TopicQueryService topicQueryService) {
        this.topicService = topicService;
        this.topicRepository = topicRepository;
        this.topicQueryService = topicQueryService;
    }

    /**
     * {@code POST  /topics} : Create a new topic.
     *
     * @param topicDTO the topicDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new topicDTO, or with status {@code 400 (Bad Request)} if the topic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/topics")
    public ResponseEntity<TopicDTO> createTopic(@RequestBody TopicDTO topicDTO) throws URISyntaxException {
        log.debug("REST request to save Topic : {}", topicDTO);
        if (topicDTO.getId() != null) {
            throw new BadRequestAlertException("A new topic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TopicDTO result = topicService.save(topicDTO);
        return ResponseEntity
            .created(new URI("/api/topics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /topics/:id} : Updates an existing topic.
     *
     * @param id the id of the topicDTO to save.
     * @param topicDTO the topicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicDTO,
     * or with status {@code 400 (Bad Request)} if the topicDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the topicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/topics/{id}")
    public ResponseEntity<TopicDTO> updateTopic(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TopicDTO topicDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Topic : {}, {}", id, topicDTO);
        if (topicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!topicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TopicDTO result = topicService.update(topicDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, topicDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /topics/:id} : Partial updates given fields of an existing topic, field will ignore if it is null
     *
     * @param id the id of the topicDTO to save.
     * @param topicDTO the topicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicDTO,
     * or with status {@code 400 (Bad Request)} if the topicDTO is not valid,
     * or with status {@code 404 (Not Found)} if the topicDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the topicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/topics/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TopicDTO> partialUpdateTopic(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TopicDTO topicDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Topic partially : {}, {}", id, topicDTO);
        if (topicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!topicRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TopicDTO> result = topicService.partialUpdate(topicDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, topicDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /topics} : get all the topics.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of topics in body.
     */
    @GetMapping("/topics")
    public ResponseEntity<List<TopicDTO>> getAllTopics(
        TopicCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Topics by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<TopicDTO> page = topicQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /topics/count} : count all the topics.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/topics/count")
    public ResponseEntity<Long> countTopics(TopicCriteria criteria) {
        log.debug("REST request to count Topics by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(topicQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /topics/:id} : get the "id" topic.
     *
     * @param id the id of the topicDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the topicDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/topics/{id}")
    public ResponseEntity<TopicDTO> getTopic(@PathVariable Long id) {
        log.debug("REST request to get Topic : {}", id);
        Optional<TopicDTO> topicDTO = topicService.findOne(id);
        return ResponseUtil.wrapOrNotFound(topicDTO);
    }

    /**
     * {@code DELETE  /topics/:id} : delete the "id" topic.
     *
     * @param id the id of the topicDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        log.debug("REST request to delete Topic : {}", id);
        topicService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/topics?query=:query} : search for the topic corresponding
     * to the query.
     *
     * @param query the query of the topic search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/topics")
    public ResponseEntity<List<TopicDTO>> searchTopics(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Topics for query {}", query);
        Page<TopicDTO> page = topicService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
