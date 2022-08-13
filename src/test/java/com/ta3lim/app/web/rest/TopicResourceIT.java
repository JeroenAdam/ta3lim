package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.Topic;
import com.ta3lim.app.repository.TopicRepository;
import com.ta3lim.app.repository.search.TopicSearchRepository;
import com.ta3lim.app.service.criteria.TopicCriteria;
import com.ta3lim.app.service.dto.TopicDTO;
import com.ta3lim.app.service.mapper.TopicMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TopicResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TopicResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/topics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/topics";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicSearchRepository topicSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTopicMockMvc;

    private Topic topic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Topic createEntity(EntityManager em) {
        Topic topic = new Topic().label(DEFAULT_LABEL).creationDate(DEFAULT_CREATION_DATE);
        return topic;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Topic createUpdatedEntity(EntityManager em) {
        Topic topic = new Topic().label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);
        return topic;
    }

    @BeforeEach
    public void initTest() {
        topicSearchRepository.deleteAll();
        topic = createEntity(em);
    }

    @Test
    @Transactional
    void createTopic() throws Exception {
        int databaseSizeBeforeCreate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);
        restTopicMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Topic testTopic = topicList.get(topicList.size() - 1);
        assertThat(testTopic.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testTopic.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createTopicWithExistingId() throws Exception {
        // Create the Topic with an existing ID
        topic.setId(1L);
        TopicDTO topicDTO = topicMapper.toDto(topic);

        int databaseSizeBeforeCreate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTopicMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTopics() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList
        restTopicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    void getTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get the topic
        restTopicMockMvc
            .perform(get(ENTITY_API_URL_ID, topic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(topic.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getTopicsByIdFiltering() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        Long id = topic.getId();

        defaultTopicShouldBeFound("id.equals=" + id);
        defaultTopicShouldNotBeFound("id.notEquals=" + id);

        defaultTopicShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTopicShouldNotBeFound("id.greaterThan=" + id);

        defaultTopicShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTopicShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTopicsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where label equals to DEFAULT_LABEL
        defaultTopicShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the topicList where label equals to UPDATED_LABEL
        defaultTopicShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllTopicsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultTopicShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the topicList where label equals to UPDATED_LABEL
        defaultTopicShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllTopicsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where label is not null
        defaultTopicShouldBeFound("label.specified=true");

        // Get all the topicList where label is null
        defaultTopicShouldNotBeFound("label.specified=false");
    }

    @Test
    @Transactional
    void getAllTopicsByLabelContainsSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where label contains DEFAULT_LABEL
        defaultTopicShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the topicList where label contains UPDATED_LABEL
        defaultTopicShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllTopicsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where label does not contain DEFAULT_LABEL
        defaultTopicShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the topicList where label does not contain UPDATED_LABEL
        defaultTopicShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate equals to DEFAULT_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the topicList where creationDate equals to UPDATED_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the topicList where creationDate equals to UPDATED_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate is not null
        defaultTopicShouldBeFound("creationDate.specified=true");

        // Get all the topicList where creationDate is null
        defaultTopicShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the topicList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the topicList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate is less than DEFAULT_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the topicList where creationDate is less than UPDATED_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topicList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultTopicShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the topicList where creationDate is greater than SMALLER_CREATION_DATE
        defaultTopicShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllTopicsByResourceIsEqualToSomething() throws Exception {
        Resource resource;
        if (TestUtil.findAll(em, Resource.class).isEmpty()) {
            topicRepository.saveAndFlush(topic);
            resource = ResourceResourceIT.createEntity(em);
        } else {
            resource = TestUtil.findAll(em, Resource.class).get(0);
        }
        em.persist(resource);
        em.flush();
        topic.addResource(resource);
        topicRepository.saveAndFlush(topic);
        Long resourceId = resource.getId();

        // Get all the topicList where resource equals to resourceId
        defaultTopicShouldBeFound("resourceId.equals=" + resourceId);

        // Get all the topicList where resource equals to (resourceId + 1)
        defaultTopicShouldNotBeFound("resourceId.equals=" + (resourceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTopicShouldBeFound(String filter) throws Exception {
        restTopicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));

        // Check, that the count call also returns 1
        restTopicMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTopicShouldNotBeFound(String filter) throws Exception {
        restTopicMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTopicMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTopic() throws Exception {
        // Get the topic
        restTopicMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        topicSearchRepository.save(topic);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());

        // Update the topic
        Topic updatedTopic = topicRepository.findById(topic.getId()).get();
        // Disconnect from session so that the updates on updatedTopic are not directly saved in db
        em.detach(updatedTopic);
        updatedTopic.label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);
        TopicDTO topicDTO = topicMapper.toDto(updatedTopic);

        restTopicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, topicDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isOk());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        Topic testTopic = topicList.get(topicList.size() - 1);
        assertThat(testTopic.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testTopic.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Topic> topicSearchList = IterableUtils.toList(topicSearchRepository.findAll());
                Topic testTopicSearch = topicSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTopicSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testTopicSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, topicDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTopicWithPatch() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        int databaseSizeBeforeUpdate = topicRepository.findAll().size();

        // Update the topic using partial update
        Topic partialUpdatedTopic = new Topic();
        partialUpdatedTopic.setId(topic.getId());

        restTopicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTopic.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTopic))
            )
            .andExpect(status().isOk());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        Topic testTopic = topicList.get(topicList.size() - 1);
        assertThat(testTopic.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testTopic.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTopicWithPatch() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        int databaseSizeBeforeUpdate = topicRepository.findAll().size();

        // Update the topic using partial update
        Topic partialUpdatedTopic = new Topic();
        partialUpdatedTopic.setId(topic.getId());

        partialUpdatedTopic.label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);

        restTopicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTopic.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTopic))
            )
            .andExpect(status().isOk());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        Topic testTopic = topicList.get(topicList.size() - 1);
        assertThat(testTopic.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testTopic.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, topicDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTopic() throws Exception {
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        topic.setId(count.incrementAndGet());

        // Create the Topic
        TopicDTO topicDTO = topicMapper.toDto(topic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTopicMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(topicDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Topic in the database
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);
        topicRepository.save(topic);
        topicSearchRepository.save(topic);

        int databaseSizeBeforeDelete = topicRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the topic
        restTopicMockMvc
            .perform(delete(ENTITY_API_URL_ID, topic.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Topic> topicList = topicRepository.findAll();
        assertThat(topicList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(topicSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTopic() throws Exception {
        // Initialize the database
        topic = topicRepository.saveAndFlush(topic);
        topicSearchRepository.save(topic);

        // Search the topic
        restTopicMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + topic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }
}
