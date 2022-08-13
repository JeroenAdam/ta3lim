package com.ta3lim.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.repository.VotesRepository;
import com.ta3lim.app.service.VotesQueryService;
import com.ta3lim.app.service.VotesService;
import com.ta3lim.app.service.criteria.VotesCriteria;
import com.ta3lim.app.service.dto.VotesDTO;
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
 * REST controller for managing {@link com.ta3lim.app.domain.Votes}.
 */
@RestController
@RequestMapping("/api")
public class VotesResource {

    private final Logger log = LoggerFactory.getLogger(VotesResource.class);

    private static final String ENTITY_NAME = "votes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VotesService votesService;

    private final VotesRepository votesRepository;

    private final VotesQueryService votesQueryService;

    public VotesResource(VotesService votesService, VotesRepository votesRepository, VotesQueryService votesQueryService) {
        this.votesService = votesService;
        this.votesRepository = votesRepository;
        this.votesQueryService = votesQueryService;
    }

    /**
     * {@code POST  /votes} : Create a new votes.
     *
     * @param votesDTO the votesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new votesDTO, or with status {@code 400 (Bad Request)} if the votes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/votes")
    public ResponseEntity<VotesDTO> createVotes(@RequestBody VotesDTO votesDTO) throws URISyntaxException {
        log.debug("REST request to save Votes : {}", votesDTO);
        if (votesDTO.getId() != null) {
            throw new BadRequestAlertException("A new votes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VotesDTO result = votesService.save(votesDTO);
        return ResponseEntity
            .created(new URI("/api/votes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /votes/:id} : Updates an existing votes.
     *
     * @param id the id of the votesDTO to save.
     * @param votesDTO the votesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated votesDTO,
     * or with status {@code 400 (Bad Request)} if the votesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the votesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/votes/{id}")
    public ResponseEntity<VotesDTO> updateVotes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VotesDTO votesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Votes : {}, {}", id, votesDTO);
        if (votesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, votesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!votesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VotesDTO result = votesService.update(votesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, votesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /votes/:id} : Partial updates given fields of an existing votes, field will ignore if it is null
     *
     * @param id the id of the votesDTO to save.
     * @param votesDTO the votesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated votesDTO,
     * or with status {@code 400 (Bad Request)} if the votesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the votesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the votesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/votes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VotesDTO> partialUpdateVotes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VotesDTO votesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Votes partially : {}, {}", id, votesDTO);
        if (votesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, votesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!votesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VotesDTO> result = votesService.partialUpdate(votesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, votesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /votes} : get all the votes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of votes in body.
     */
    @GetMapping("/votes")
    public ResponseEntity<List<VotesDTO>> getAllVotes(
        VotesCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Votes by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<VotesDTO> page = votesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /votes/count} : count all the votes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/votes/count")
    public ResponseEntity<Long> countVotes(VotesCriteria criteria) {
        log.debug("REST request to count Votes by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(votesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /votes/:id} : get the "id" votes.
     *
     * @param id the id of the votesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the votesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/votes/{id}")
    public ResponseEntity<VotesDTO> getVotes(@PathVariable Long id) {
        log.debug("REST request to get Votes : {}", id);
        Optional<VotesDTO> votesDTO = votesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(votesDTO);
    }

    /**
     * {@code DELETE  /votes/:id} : delete the "id" votes.
     *
     * @param id the id of the votesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/votes/{id}")
    public ResponseEntity<Void> deleteVotes(@PathVariable Long id) {
        log.debug("REST request to delete Votes : {}", id);
        votesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/votes?query=:query} : search for the votes corresponding
     * to the query.
     *
     * @param query the query of the votes search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/votes")
    public ResponseEntity<List<VotesDTO>> searchVotes(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Votes for query {}", query);
        Page<VotesDTO> page = votesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
