package com.ta3lim.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.repository.SubjectRepository;
import com.ta3lim.app.service.SubjectQueryService;
import com.ta3lim.app.service.SubjectService;
import com.ta3lim.app.service.criteria.SubjectCriteria;
import com.ta3lim.app.service.dto.SubjectDTO;
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
 * REST controller for managing {@link com.ta3lim.app.domain.Subject}.
 */
@RestController
@RequestMapping("/api")
public class SubjectResource {

    private final Logger log = LoggerFactory.getLogger(SubjectResource.class);

    private static final String ENTITY_NAME = "subject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubjectService subjectService;

    private final SubjectRepository subjectRepository;

    private final SubjectQueryService subjectQueryService;

    public SubjectResource(SubjectService subjectService, SubjectRepository subjectRepository, SubjectQueryService subjectQueryService) {
        this.subjectService = subjectService;
        this.subjectRepository = subjectRepository;
        this.subjectQueryService = subjectQueryService;
    }

    /**
     * {@code POST  /subjects} : Create a new subject.
     *
     * @param subjectDTO the subjectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subjectDTO, or with status {@code 400 (Bad Request)} if the subject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subjects")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO subjectDTO) throws URISyntaxException {
        log.debug("REST request to save Subject : {}", subjectDTO);
        if (subjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new subject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubjectDTO result = subjectService.save(subjectDTO);
        return ResponseEntity
            .created(new URI("/api/subjects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subjects/:id} : Updates an existing subject.
     *
     * @param id the id of the subjectDTO to save.
     * @param subjectDTO the subjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subjectDTO,
     * or with status {@code 400 (Bad Request)} if the subjectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SubjectDTO subjectDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Subject : {}, {}", id, subjectDTO);
        if (subjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubjectDTO result = subjectService.update(subjectDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subjectDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subjects/:id} : Partial updates given fields of an existing subject, field will ignore if it is null
     *
     * @param id the id of the subjectDTO to save.
     * @param subjectDTO the subjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subjectDTO,
     * or with status {@code 400 (Bad Request)} if the subjectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subjectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/subjects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubjectDTO> partialUpdateSubject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SubjectDTO subjectDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Subject partially : {}, {}", id, subjectDTO);
        if (subjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubjectDTO> result = subjectService.partialUpdate(subjectDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subjectDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subjects} : get all the subjects.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subjects in body.
     */
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(
        SubjectCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Subjects by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<SubjectDTO> page = subjectQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subjects/count} : count all the subjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/subjects/count")
    public ResponseEntity<Long> countSubjects(SubjectCriteria criteria) {
        log.debug("REST request to count Subjects by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(subjectQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /subjects/:id} : get the "id" subject.
     *
     * @param id the id of the subjectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subjectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/subjects/{id}")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable Long id) {
        log.debug("REST request to get Subject : {}", id);
        Optional<SubjectDTO> subjectDTO = subjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subjectDTO);
    }

    /**
     * {@code DELETE  /subjects/:id} : delete the "id" subject.
     *
     * @param id the id of the subjectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        log.debug("REST request to delete Subject : {}", id);
        subjectService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/subjects?query=:query} : search for the subject corresponding
     * to the query.
     *
     * @param query the query of the subject search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/subjects")
    public ResponseEntity<List<SubjectDTO>> searchSubjects(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Subjects for query {}", query);
        Page<SubjectDTO> page = subjectService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
