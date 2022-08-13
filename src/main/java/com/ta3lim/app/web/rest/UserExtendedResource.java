package com.ta3lim.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.repository.UserExtendedRepository;
import com.ta3lim.app.service.UserExtendedQueryService;
import com.ta3lim.app.service.UserExtendedService;
import com.ta3lim.app.service.criteria.UserExtendedCriteria;
import com.ta3lim.app.service.dto.UserExtendedDTO;
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
 * REST controller for managing {@link com.ta3lim.app.domain.UserExtended}.
 */
@RestController
@RequestMapping("/api")
public class UserExtendedResource {

    private final Logger log = LoggerFactory.getLogger(UserExtendedResource.class);

    private static final String ENTITY_NAME = "userExtended";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserExtendedService userExtendedService;

    private final UserExtendedRepository userExtendedRepository;

    private final UserExtendedQueryService userExtendedQueryService;

    public UserExtendedResource(
        UserExtendedService userExtendedService,
        UserExtendedRepository userExtendedRepository,
        UserExtendedQueryService userExtendedQueryService
    ) {
        this.userExtendedService = userExtendedService;
        this.userExtendedRepository = userExtendedRepository;
        this.userExtendedQueryService = userExtendedQueryService;
    }

    /**
     * {@code POST  /user-extendeds} : Create a new userExtended.
     *
     * @param userExtendedDTO the userExtendedDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userExtendedDTO, or with status {@code 400 (Bad Request)} if the userExtended has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-extendeds")
    public ResponseEntity<UserExtendedDTO> createUserExtended(@RequestBody UserExtendedDTO userExtendedDTO) throws URISyntaxException {
        log.debug("REST request to save UserExtended : {}", userExtendedDTO);
        if (userExtendedDTO.getId() != null) {
            throw new BadRequestAlertException("A new userExtended cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserExtendedDTO result = userExtendedService.save(userExtendedDTO);
        return ResponseEntity
            .created(new URI("/api/user-extendeds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-extendeds/:id} : Updates an existing userExtended.
     *
     * @param id the id of the userExtendedDTO to save.
     * @param userExtendedDTO the userExtendedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtendedDTO,
     * or with status {@code 400 (Bad Request)} if the userExtendedDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userExtendedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-extendeds/{id}")
    public ResponseEntity<UserExtendedDTO> updateUserExtended(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExtendedDTO userExtendedDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserExtended : {}, {}", id, userExtendedDTO);
        if (userExtendedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtendedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtendedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserExtendedDTO result = userExtendedService.update(userExtendedDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtendedDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-extendeds/:id} : Partial updates given fields of an existing userExtended, field will ignore if it is null
     *
     * @param id the id of the userExtendedDTO to save.
     * @param userExtendedDTO the userExtendedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtendedDTO,
     * or with status {@code 400 (Bad Request)} if the userExtendedDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userExtendedDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userExtendedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-extendeds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserExtendedDTO> partialUpdateUserExtended(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserExtendedDTO userExtendedDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserExtended partially : {}, {}", id, userExtendedDTO);
        if (userExtendedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtendedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtendedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserExtendedDTO> result = userExtendedService.partialUpdate(userExtendedDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userExtendedDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-extendeds} : get all the userExtendeds.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userExtendeds in body.
     */
    @GetMapping("/user-extendeds")
    public ResponseEntity<List<UserExtendedDTO>> getAllUserExtendeds(
        UserExtendedCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get UserExtendeds by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<UserExtendedDTO> page = userExtendedQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-extendeds/count} : count all the userExtendeds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-extendeds/count")
    public ResponseEntity<Long> countUserExtendeds(UserExtendedCriteria criteria) {
        log.debug("REST request to count UserExtendeds by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(userExtendedQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-extendeds/:id} : get the "id" userExtended.
     *
     * @param id the id of the userExtendedDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userExtendedDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-extendeds/{id}")
    public ResponseEntity<UserExtendedDTO> getUserExtended(@PathVariable Long id) {
        log.debug("REST request to get UserExtended : {}", id);
        Optional<UserExtendedDTO> userExtendedDTO = userExtendedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userExtendedDTO);
    }

    /**
     * {@code DELETE  /user-extendeds/:id} : delete the "id" userExtended.
     *
     * @param id the id of the userExtendedDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-extendeds/{id}")
    public ResponseEntity<Void> deleteUserExtended(@PathVariable Long id) {
        log.debug("REST request to delete UserExtended : {}", id);
        userExtendedService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/user-extendeds?query=:query} : search for the userExtended corresponding
     * to the query.
     *
     * @param query the query of the userExtended search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-extendeds")
    public ResponseEntity<List<UserExtendedDTO>> searchUserExtendeds(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of UserExtendeds for query {}", query);
        Page<UserExtendedDTO> page = userExtendedService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
