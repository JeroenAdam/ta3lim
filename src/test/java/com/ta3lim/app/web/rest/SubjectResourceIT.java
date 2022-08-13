package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.repository.SubjectRepository;
import com.ta3lim.app.repository.search.SubjectSearchRepository;
import com.ta3lim.app.service.criteria.SubjectCriteria;
import com.ta3lim.app.service.dto.SubjectDTO;
import com.ta3lim.app.service.mapper.SubjectMapper;
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
 * Integration tests for the {@link SubjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubjectResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/subjects";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private SubjectSearchRepository subjectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubjectMockMvc;

    private Subject subject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createEntity(EntityManager em) {
        Subject subject = new Subject().label(DEFAULT_LABEL).creationDate(DEFAULT_CREATION_DATE);
        return subject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createUpdatedEntity(EntityManager em) {
        Subject subject = new Subject().label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);
        return subject;
    }

    @BeforeEach
    public void initTest() {
        subjectSearchRepository.deleteAll();
        subject = createEntity(em);
    }

    @Test
    @Transactional
    void createSubject() throws Exception {
        int databaseSizeBeforeCreate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);
        restSubjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testSubject.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createSubjectWithExistingId() throws Exception {
        // Create the Subject with an existing ID
        subject.setId(1L);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        int databaseSizeBeforeCreate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubjectMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSubjects() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    void getSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get the subject
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL_ID, subject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subject.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getSubjectsByIdFiltering() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        Long id = subject.getId();

        defaultSubjectShouldBeFound("id.equals=" + id);
        defaultSubjectShouldNotBeFound("id.notEquals=" + id);

        defaultSubjectShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSubjectShouldNotBeFound("id.greaterThan=" + id);

        defaultSubjectShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSubjectShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSubjectsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where label equals to DEFAULT_LABEL
        defaultSubjectShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the subjectList where label equals to UPDATED_LABEL
        defaultSubjectShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllSubjectsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultSubjectShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the subjectList where label equals to UPDATED_LABEL
        defaultSubjectShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllSubjectsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where label is not null
        defaultSubjectShouldBeFound("label.specified=true");

        // Get all the subjectList where label is null
        defaultSubjectShouldNotBeFound("label.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByLabelContainsSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where label contains DEFAULT_LABEL
        defaultSubjectShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the subjectList where label contains UPDATED_LABEL
        defaultSubjectShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllSubjectsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where label does not contain DEFAULT_LABEL
        defaultSubjectShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the subjectList where label does not contain UPDATED_LABEL
        defaultSubjectShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate equals to DEFAULT_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the subjectList where creationDate equals to UPDATED_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the subjectList where creationDate equals to UPDATED_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate is not null
        defaultSubjectShouldBeFound("creationDate.specified=true");

        // Get all the subjectList where creationDate is null
        defaultSubjectShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the subjectList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the subjectList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate is less than DEFAULT_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the subjectList where creationDate is less than UPDATED_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllSubjectsByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultSubjectShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the subjectList where creationDate is greater than SMALLER_CREATION_DATE
        defaultSubjectShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubjectShouldBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));

        // Check, that the count call also returns 1
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSubjectShouldNotBeFound(String filter) throws Exception {
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSubjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSubject() throws Exception {
        // Get the subject
        restSubjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        subjectSearchRepository.save(subject);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());

        // Update the subject
        Subject updatedSubject = subjectRepository.findById(subject.getId()).get();
        // Disconnect from session so that the updates on updatedSubject are not directly saved in db
        em.detach(updatedSubject);
        updatedSubject.label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);
        SubjectDTO subjectDTO = subjectMapper.toDto(updatedSubject);

        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testSubject.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Subject> subjectSearchList = IterableUtils.toList(subjectSearchRepository.findAll());
                Subject testSubjectSearch = subjectSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSubjectSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testSubjectSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjectDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.label(UPDATED_LABEL);

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testSubject.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateSubjectWithPatch() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject using partial update
        Subject partialUpdatedSubject = new Subject();
        partialUpdatedSubject.setId(subject.getId());

        partialUpdatedSubject.label(UPDATED_LABEL).creationDate(UPDATED_CREATION_DATE);

        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubject.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubject))
            )
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testSubject.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subjectDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        subject.setId(count.incrementAndGet());

        // Create the Subject
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjectDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);
        subjectRepository.save(subject);
        subjectSearchRepository.save(subject);

        int databaseSizeBeforeDelete = subjectRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subject
        restSubjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, subject.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subjectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSubject() throws Exception {
        // Initialize the database
        subject = subjectRepository.saveAndFlush(subject);
        subjectSearchRepository.save(subject);

        // Search the subject
        restSubjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + subject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }
}
