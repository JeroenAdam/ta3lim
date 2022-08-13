package com.ta3lim.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.repository.MessageRepository;
import com.ta3lim.app.service.MessageQueryService;
import com.ta3lim.app.service.MessageService;
import com.ta3lim.app.service.criteria.MessageCriteria;
import com.ta3lim.app.service.dto.MessageDTO;
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
 * REST controller for managing {@link com.ta3lim.app.domain.Message}.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    private static final String ENTITY_NAME = "message";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MessageService messageService;

    private final MessageRepository messageRepository;

    private final MessageQueryService messageQueryService;

    public MessageResource(MessageService messageService, MessageRepository messageRepository, MessageQueryService messageQueryService) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.messageQueryService = messageQueryService;
    }

    /**
     * {@code POST  /messages} : Create a new message.
     *
     * @param messageDTO the messageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new messageDTO, or with status {@code 400 (Bad Request)} if the message has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) throws URISyntaxException {
        log.debug("REST request to save Message : {}", messageDTO);
        if (messageDTO.getId() != null) {
            throw new BadRequestAlertException("A new message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MessageDTO result = messageService.save(messageDTO);
        return ResponseEntity
            .created(new URI("/api/messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /messages/:id} : Updates an existing message.
     *
     * @param id the id of the messageDTO to save.
     * @param messageDTO the messageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageDTO,
     * or with status {@code 400 (Bad Request)} if the messageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the messageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> updateMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MessageDTO messageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Message : {}, {}", id, messageDTO);
        if (messageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, messageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!messageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MessageDTO result = messageService.update(messageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, messageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /messages/:id} : Partial updates given fields of an existing message, field will ignore if it is null
     *
     * @param id the id of the messageDTO to save.
     * @param messageDTO the messageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageDTO,
     * or with status {@code 400 (Bad Request)} if the messageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the messageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the messageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/messages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MessageDTO> partialUpdateMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MessageDTO messageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Message partially : {}, {}", id, messageDTO);
        if (messageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, messageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!messageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MessageDTO> result = messageService.partialUpdate(messageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, messageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /messages} : get all the messages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of messages in body.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getAllMessages(
        MessageCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Messages by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<MessageDTO> page = messageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /messages/count} : count all the messages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/messages/count")
    public ResponseEntity<Long> countMessages(MessageCriteria criteria) {
        log.debug("REST request to count Messages by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(messageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /messages/:id} : get the "id" message.
     *
     * @param id the id of the messageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the messageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        Optional<MessageDTO> messageDTO = messageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(messageDTO);
    }

    /**
     * {@code DELETE  /messages/:id} : delete the "id" message.
     *
     * @param id the id of the messageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/messages?query=:query} : search for the message corresponding
     * to the query.
     *
     * @param query the query of the message search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/messages")
    public ResponseEntity<List<MessageDTO>> searchMessages(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Messages for query {}", query);
        Page<MessageDTO> page = messageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
