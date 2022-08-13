package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.repository.FavoriteRepository;
import com.ta3lim.app.repository.search.FavoriteSearchRepository;
import com.ta3lim.app.service.FavoriteService;
import com.ta3lim.app.service.criteria.FavoriteCriteria;
import com.ta3lim.app.service.dto.FavoriteDTO;
import com.ta3lim.app.service.mapper.FavoriteMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link FavoriteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FavoriteResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/favorites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/favorites";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteRepository favoriteRepositoryMock;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Mock
    private FavoriteService favoriteServiceMock;

    @Autowired
    private FavoriteSearchRepository favoriteSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFavoriteMockMvc;

    private Favorite favorite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createEntity(EntityManager em) {
        Favorite favorite = new Favorite().creationDate(DEFAULT_CREATION_DATE);
        return favorite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createUpdatedEntity(EntityManager em) {
        Favorite favorite = new Favorite().creationDate(UPDATED_CREATION_DATE);
        return favorite;
    }

    @BeforeEach
    public void initTest() {
        favoriteSearchRepository.deleteAll();
        favorite = createEntity(em);
    }

    @Test
    @Transactional
    void createFavorite() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);
        restFavoriteMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createFavoriteWithExistingId() throws Exception {
        // Create the Favorite with an existing ID
        favorite.setId(1L);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFavoriteMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFavorites() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(favoriteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL_ID, favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getFavoritesByIdFiltering() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        Long id = favorite.getId();

        defaultFavoriteShouldBeFound("id.equals=" + id);
        defaultFavoriteShouldNotBeFound("id.notEquals=" + id);

        defaultFavoriteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFavoriteShouldNotBeFound("id.greaterThan=" + id);

        defaultFavoriteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFavoriteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate equals to DEFAULT_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the favoriteList where creationDate equals to UPDATED_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the favoriteList where creationDate equals to UPDATED_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate is not null
        defaultFavoriteShouldBeFound("creationDate.specified=true");

        // Get all the favoriteList where creationDate is null
        defaultFavoriteShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the favoriteList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the favoriteList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate is less than DEFAULT_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the favoriteList where creationDate is less than UPDATED_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultFavoriteShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the favoriteList where creationDate is greater than SMALLER_CREATION_DATE
        defaultFavoriteShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllFavoritesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            favoriteRepository.saveAndFlush(favorite);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        favorite.setUser(user);
        favoriteRepository.saveAndFlush(favorite);
        String userId = user.getId();

        // Get all the favoriteList where user equals to userId
        defaultFavoriteShouldBeFound("userId.equals=" + userId);

        // Get all the favoriteList where user equals to "invalid-id"
        defaultFavoriteShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllFavoritesByResourceIsEqualToSomething() throws Exception {
        Resource resource;
        if (TestUtil.findAll(em, Resource.class).isEmpty()) {
            favoriteRepository.saveAndFlush(favorite);
            resource = ResourceResourceIT.createEntity(em);
        } else {
            resource = TestUtil.findAll(em, Resource.class).get(0);
        }
        em.persist(resource);
        em.flush();
        favorite.setResource(resource);
        favoriteRepository.saveAndFlush(favorite);
        Long resourceId = resource.getId();

        // Get all the favoriteList where resource equals to resourceId
        defaultFavoriteShouldBeFound("resourceId.equals=" + resourceId);

        // Get all the favoriteList where resource equals to (resourceId + 1)
        defaultFavoriteShouldNotBeFound("resourceId.equals=" + (resourceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFavoriteShouldBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));

        // Check, that the count call also returns 1
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFavoriteShouldNotBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favoriteSearchRepository.save(favorite);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());

        // Update the favorite
        Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).get();
        // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
        em.detach(updatedFavorite);
        updatedFavorite.creationDate(UPDATED_CREATION_DATE);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(updatedFavorite);

        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Favorite> favoriteSearchList = IterableUtils.toList(favoriteSearchRepository.findAll());
                Favorite testFavoriteSearch = favoriteSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFavoriteSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.creationDate(UPDATED_CREATION_DATE);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.creationDate(UPDATED_CREATION_DATE);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);
        favoriteRepository.save(favorite);
        favoriteSearchRepository.save(favorite);

        int databaseSizeBeforeDelete = favoriteRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the favorite
        restFavoriteMockMvc
            .perform(delete(ENTITY_API_URL_ID, favorite.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(favoriteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFavorite() throws Exception {
        // Initialize the database
        favorite = favoriteRepository.saveAndFlush(favorite);
        favoriteSearchRepository.save(favorite);

        // Search the favorite
        restFavoriteMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }
}
