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
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.Votes;
import com.ta3lim.app.repository.VotesRepository;
import com.ta3lim.app.repository.search.VotesSearchRepository;
import com.ta3lim.app.service.VotesService;
import com.ta3lim.app.service.criteria.VotesCriteria;
import com.ta3lim.app.service.dto.VotesDTO;
import com.ta3lim.app.service.mapper.VotesMapper;
import java.util.ArrayList;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Integration tests for the {@link VotesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VotesResourceIT {

    private static final String ENTITY_API_URL = "/api/votes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/votes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VotesRepository votesRepository;

    @Mock
    private VotesRepository votesRepositoryMock;

    @Autowired
    private VotesMapper votesMapper;

    @Mock
    private VotesService votesServiceMock;

    @Autowired
    private VotesSearchRepository votesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVotesMockMvc;

    private Votes votes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Votes createEntity(EntityManager em) {
        Votes votes = new Votes();
        return votes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Votes createUpdatedEntity(EntityManager em) {
        Votes votes = new Votes();
        return votes;
    }

    @BeforeEach
    public void initTest() {
        votesSearchRepository.deleteAll();
        votes = createEntity(em);
    }

    @Test
    @Transactional
    void createVotes() throws Exception {
        int databaseSizeBeforeCreate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);
        restVotesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Votes testVotes = votesList.get(votesList.size() - 1);
    }

    @Test
    @Transactional
    void createVotesWithExistingId() throws Exception {
        // Create the Votes with an existing ID
        votes.setId(1L);
        VotesDTO votesDTO = votesMapper.toDto(votes);

        int databaseSizeBeforeCreate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restVotesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllVotes() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        // Get all the votesList
        restVotesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(votes.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVotesWithEagerRelationshipsIsEnabled() throws Exception {
        when(votesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVotesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(votesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVotesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(votesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVotesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(votesRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVotes() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        // Get the votes
        restVotesMockMvc
            .perform(get(ENTITY_API_URL_ID, votes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(votes.getId().intValue()));
    }

    @Test
    @Transactional
    void getVotesByIdFiltering() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        Long id = votes.getId();

        defaultVotesShouldBeFound("id.equals=" + id);
        defaultVotesShouldNotBeFound("id.notEquals=" + id);

        defaultVotesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVotesShouldNotBeFound("id.greaterThan=" + id);

        defaultVotesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVotesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVotesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            votesRepository.saveAndFlush(votes);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        votes.setUser(user);
        votesRepository.saveAndFlush(votes);
        String userId = user.getId();

        // Get all the votesList where user equals to userId
        defaultVotesShouldBeFound("userId.equals=" + userId);

        // Get all the votesList where user equals to "invalid-id"
        defaultVotesShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllVotesByResourceIsEqualToSomething() throws Exception {
        Resource resource;
        if (TestUtil.findAll(em, Resource.class).isEmpty()) {
            votesRepository.saveAndFlush(votes);
            resource = ResourceResourceIT.createEntity(em);
        } else {
            resource = TestUtil.findAll(em, Resource.class).get(0);
        }
        em.persist(resource);
        em.flush();
        votes.setResource(resource);
        votesRepository.saveAndFlush(votes);
        Long resourceId = resource.getId();

        // Get all the votesList where resource equals to resourceId
        defaultVotesShouldBeFound("resourceId.equals=" + resourceId);

        // Get all the votesList where resource equals to (resourceId + 1)
        defaultVotesShouldNotBeFound("resourceId.equals=" + (resourceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVotesShouldBeFound(String filter) throws Exception {
        restVotesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(votes.getId().intValue())));

        // Check, that the count call also returns 1
        restVotesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVotesShouldNotBeFound(String filter) throws Exception {
        restVotesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVotesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVotes() throws Exception {
        // Get the votes
        restVotesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVotes() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        votesSearchRepository.save(votes);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());

        // Update the votes
        Votes updatedVotes = votesRepository.findById(votes.getId()).get();
        // Disconnect from session so that the updates on updatedVotes are not directly saved in db
        em.detach(updatedVotes);
        VotesDTO votesDTO = votesMapper.toDto(updatedVotes);

        restVotesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, votesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isOk());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        Votes testVotes = votesList.get(votesList.size() - 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Votes> votesSearchList = IterableUtils.toList(votesSearchRepository.findAll());
                Votes testVotesSearch = votesSearchList.get(searchDatabaseSizeAfter - 1);
            });
    }

    @Test
    @Transactional
    void putNonExistingVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, votesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateVotesWithPatch() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        int databaseSizeBeforeUpdate = votesRepository.findAll().size();

        // Update the votes using partial update
        Votes partialUpdatedVotes = new Votes();
        partialUpdatedVotes.setId(votes.getId());

        restVotesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVotes.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVotes))
            )
            .andExpect(status().isOk());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        Votes testVotes = votesList.get(votesList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateVotesWithPatch() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);

        int databaseSizeBeforeUpdate = votesRepository.findAll().size();

        // Update the votes using partial update
        Votes partialUpdatedVotes = new Votes();
        partialUpdatedVotes.setId(votes.getId());

        restVotesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVotes.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVotes))
            )
            .andExpect(status().isOk());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        Votes testVotes = votesList.get(votesList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, votesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVotes() throws Exception {
        int databaseSizeBeforeUpdate = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        votes.setId(count.incrementAndGet());

        // Create the Votes
        VotesDTO votesDTO = votesMapper.toDto(votes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVotesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(votesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Votes in the database
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteVotes() throws Exception {
        // Initialize the database
        votesRepository.saveAndFlush(votes);
        votesRepository.save(votes);
        votesSearchRepository.save(votes);

        int databaseSizeBeforeDelete = votesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the votes
        restVotesMockMvc
            .perform(delete(ENTITY_API_URL_ID, votes.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Votes> votesList = votesRepository.findAll();
        assertThat(votesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(votesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchVotes() throws Exception {
        // Initialize the database
        votes = votesRepository.saveAndFlush(votes);
        votesSearchRepository.save(votes);

        // Search the votes
        restVotesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + votes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(votes.getId().intValue())));
    }
}
