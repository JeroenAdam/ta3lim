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
import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.domain.Topic;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.enumeration.AgeRange;
import com.ta3lim.app.domain.enumeration.ResourceType;
import com.ta3lim.app.repository.ResourceRepository;
import com.ta3lim.app.repository.search.ResourceSearchRepository;
import com.ta3lim.app.service.ResourceService;
import com.ta3lim.app.service.criteria.ResourceCriteria;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.mapper.ResourceMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ResourceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ResourceResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ResourceType DEFAULT_RESOURCE_TYPE = ResourceType.ARTICLES;
    private static final ResourceType UPDATED_RESOURCE_TYPE = ResourceType.DOCUMENTS;

    private static final AgeRange DEFAULT_ANGE_RAGE = AgeRange.AGE_ALL;
    private static final AgeRange UPDATED_ANGE_RAGE = AgeRange.AGE_04_06;

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_UPDATED = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final Long DEFAULT_VIEWS = 1L;
    private static final Long UPDATED_VIEWS = 2L;
    private static final Long SMALLER_VIEWS = 1L - 1L;

    private static final Long DEFAULT_VOTES = 1L;
    private static final Long UPDATED_VOTES = 2L;
    private static final Long SMALLER_VOTES = 1L - 1L;

    private static final String DEFAULT_APPROVED_BY = "AAAAAAAAAA";
    private static final String UPDATED_APPROVED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/resources";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceRepository resourceRepositoryMock;

    @Autowired
    private ResourceMapper resourceMapper;

    @Mock
    private ResourceService resourceServiceMock;

    @Autowired
    private ResourceSearchRepository resourceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResourceMockMvc;

    private Resource resource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createEntity(EntityManager em) {
        Resource resource = new Resource()
            .title(DEFAULT_TITLE)
            .creationDate(DEFAULT_CREATION_DATE)
            .description(DEFAULT_DESCRIPTION)
            .resourceType(DEFAULT_RESOURCE_TYPE)
            .angeRage(DEFAULT_ANGE_RAGE)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .url(DEFAULT_URL)
            .author(DEFAULT_AUTHOR)
            .lastUpdated(DEFAULT_LAST_UPDATED)
            .activated(DEFAULT_ACTIVATED)
            .views(DEFAULT_VIEWS)
            .votes(DEFAULT_VOTES)
            .approvedBy(DEFAULT_APPROVED_BY);
        return resource;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createUpdatedEntity(EntityManager em) {
        Resource resource = new Resource()
            .title(UPDATED_TITLE)
            .creationDate(UPDATED_CREATION_DATE)
            .description(UPDATED_DESCRIPTION)
            .resourceType(UPDATED_RESOURCE_TYPE)
            .angeRage(UPDATED_ANGE_RAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL)
            .author(UPDATED_AUTHOR)
            .lastUpdated(UPDATED_LAST_UPDATED)
            .activated(UPDATED_ACTIVATED)
            .views(UPDATED_VIEWS)
            .votes(UPDATED_VOTES)
            .approvedBy(UPDATED_APPROVED_BY);
        return resource;
    }

    @BeforeEach
    public void initTest() {
        resourceSearchRepository.deleteAll();
        resource = createEntity(em);
    }

    @Test
    @Transactional
    void createResource() throws Exception {
        int databaseSizeBeforeCreate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);
        restResourceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testResource.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testResource.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testResource.getResourceType()).isEqualTo(DEFAULT_RESOURCE_TYPE);
        assertThat(testResource.getAngeRage()).isEqualTo(DEFAULT_ANGE_RAGE);
        assertThat(testResource.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testResource.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
        assertThat(testResource.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testResource.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testResource.getLastUpdated()).isEqualTo(DEFAULT_LAST_UPDATED);
        assertThat(testResource.getActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testResource.getViews()).isEqualTo(DEFAULT_VIEWS);
        assertThat(testResource.getVotes()).isEqualTo(DEFAULT_VOTES);
        assertThat(testResource.getApprovedBy()).isEqualTo(DEFAULT_APPROVED_BY);
    }

    @Test
    @Transactional
    void createResourceWithExistingId() throws Exception {
        // Create the Resource with an existing ID
        resource.setId(1L);
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        int databaseSizeBeforeCreate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restResourceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        // set the field null
        resource.setTitle(null);

        // Create the Resource, which fails.
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        restResourceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllResources() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].angeRage").value(hasItem(DEFAULT_ANGE_RAGE.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].votes").value(hasItem(DEFAULT_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResourcesWithEagerRelationshipsIsEnabled() throws Exception {
        when(resourceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResourceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(resourceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResourcesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(resourceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restResourceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(resourceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get the resource
        restResourceMockMvc
            .perform(get(ENTITY_API_URL_ID, resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(resource.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.resourceType").value(DEFAULT_RESOURCE_TYPE.toString()))
            .andExpect(jsonPath("$.angeRage").value(DEFAULT_ANGE_RAGE.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS.intValue()))
            .andExpect(jsonPath("$.votes").value(DEFAULT_VOTES.intValue()))
            .andExpect(jsonPath("$.approvedBy").value(DEFAULT_APPROVED_BY));
    }

    @Test
    @Transactional
    void getResourcesByIdFiltering() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        Long id = resource.getId();

        defaultResourceShouldBeFound("id.equals=" + id);
        defaultResourceShouldNotBeFound("id.notEquals=" + id);

        defaultResourceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResourceShouldNotBeFound("id.greaterThan=" + id);

        defaultResourceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResourceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResourcesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where title equals to DEFAULT_TITLE
        defaultResourceShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the resourceList where title equals to UPDATED_TITLE
        defaultResourceShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllResourcesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultResourceShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the resourceList where title equals to UPDATED_TITLE
        defaultResourceShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllResourcesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where title is not null
        defaultResourceShouldBeFound("title.specified=true");

        // Get all the resourceList where title is null
        defaultResourceShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByTitleContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where title contains DEFAULT_TITLE
        defaultResourceShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the resourceList where title contains UPDATED_TITLE
        defaultResourceShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllResourcesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where title does not contain DEFAULT_TITLE
        defaultResourceShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the resourceList where title does not contain UPDATED_TITLE
        defaultResourceShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate equals to DEFAULT_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the resourceList where creationDate equals to UPDATED_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the resourceList where creationDate equals to UPDATED_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate is not null
        defaultResourceShouldBeFound("creationDate.specified=true");

        // Get all the resourceList where creationDate is null
        defaultResourceShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the resourceList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the resourceList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate is less than DEFAULT_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the resourceList where creationDate is less than UPDATED_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultResourceShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the resourceList where creationDate is greater than SMALLER_CREATION_DATE
        defaultResourceShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllResourcesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where description equals to DEFAULT_DESCRIPTION
        defaultResourceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the resourceList where description equals to UPDATED_DESCRIPTION
        defaultResourceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllResourcesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultResourceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the resourceList where description equals to UPDATED_DESCRIPTION
        defaultResourceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllResourcesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where description is not null
        defaultResourceShouldBeFound("description.specified=true");

        // Get all the resourceList where description is null
        defaultResourceShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where description contains DEFAULT_DESCRIPTION
        defaultResourceShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the resourceList where description contains UPDATED_DESCRIPTION
        defaultResourceShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllResourcesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where description does not contain DEFAULT_DESCRIPTION
        defaultResourceShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the resourceList where description does not contain UPDATED_DESCRIPTION
        defaultResourceShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllResourcesByResourceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where resourceType equals to DEFAULT_RESOURCE_TYPE
        defaultResourceShouldBeFound("resourceType.equals=" + DEFAULT_RESOURCE_TYPE);

        // Get all the resourceList where resourceType equals to UPDATED_RESOURCE_TYPE
        defaultResourceShouldNotBeFound("resourceType.equals=" + UPDATED_RESOURCE_TYPE);
    }

    @Test
    @Transactional
    void getAllResourcesByResourceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where resourceType in DEFAULT_RESOURCE_TYPE or UPDATED_RESOURCE_TYPE
        defaultResourceShouldBeFound("resourceType.in=" + DEFAULT_RESOURCE_TYPE + "," + UPDATED_RESOURCE_TYPE);

        // Get all the resourceList where resourceType equals to UPDATED_RESOURCE_TYPE
        defaultResourceShouldNotBeFound("resourceType.in=" + UPDATED_RESOURCE_TYPE);
    }

    @Test
    @Transactional
    void getAllResourcesByResourceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where resourceType is not null
        defaultResourceShouldBeFound("resourceType.specified=true");

        // Get all the resourceList where resourceType is null
        defaultResourceShouldNotBeFound("resourceType.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByAngeRageIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where angeRage equals to DEFAULT_ANGE_RAGE
        defaultResourceShouldBeFound("angeRage.equals=" + DEFAULT_ANGE_RAGE);

        // Get all the resourceList where angeRage equals to UPDATED_ANGE_RAGE
        defaultResourceShouldNotBeFound("angeRage.equals=" + UPDATED_ANGE_RAGE);
    }

    @Test
    @Transactional
    void getAllResourcesByAngeRageIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where angeRage in DEFAULT_ANGE_RAGE or UPDATED_ANGE_RAGE
        defaultResourceShouldBeFound("angeRage.in=" + DEFAULT_ANGE_RAGE + "," + UPDATED_ANGE_RAGE);

        // Get all the resourceList where angeRage equals to UPDATED_ANGE_RAGE
        defaultResourceShouldNotBeFound("angeRage.in=" + UPDATED_ANGE_RAGE);
    }

    @Test
    @Transactional
    void getAllResourcesByAngeRageIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where angeRage is not null
        defaultResourceShouldBeFound("angeRage.specified=true");

        // Get all the resourceList where angeRage is null
        defaultResourceShouldNotBeFound("angeRage.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where url equals to DEFAULT_URL
        defaultResourceShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the resourceList where url equals to UPDATED_URL
        defaultResourceShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllResourcesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where url in DEFAULT_URL or UPDATED_URL
        defaultResourceShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the resourceList where url equals to UPDATED_URL
        defaultResourceShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllResourcesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where url is not null
        defaultResourceShouldBeFound("url.specified=true");

        // Get all the resourceList where url is null
        defaultResourceShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByUrlContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where url contains DEFAULT_URL
        defaultResourceShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the resourceList where url contains UPDATED_URL
        defaultResourceShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllResourcesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where url does not contain DEFAULT_URL
        defaultResourceShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the resourceList where url does not contain UPDATED_URL
        defaultResourceShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllResourcesByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where author equals to DEFAULT_AUTHOR
        defaultResourceShouldBeFound("author.equals=" + DEFAULT_AUTHOR);

        // Get all the resourceList where author equals to UPDATED_AUTHOR
        defaultResourceShouldNotBeFound("author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllResourcesByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where author in DEFAULT_AUTHOR or UPDATED_AUTHOR
        defaultResourceShouldBeFound("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR);

        // Get all the resourceList where author equals to UPDATED_AUTHOR
        defaultResourceShouldNotBeFound("author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllResourcesByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where author is not null
        defaultResourceShouldBeFound("author.specified=true");

        // Get all the resourceList where author is null
        defaultResourceShouldNotBeFound("author.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByAuthorContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where author contains DEFAULT_AUTHOR
        defaultResourceShouldBeFound("author.contains=" + DEFAULT_AUTHOR);

        // Get all the resourceList where author contains UPDATED_AUTHOR
        defaultResourceShouldNotBeFound("author.contains=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllResourcesByAuthorNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where author does not contain DEFAULT_AUTHOR
        defaultResourceShouldNotBeFound("author.doesNotContain=" + DEFAULT_AUTHOR);

        // Get all the resourceList where author does not contain UPDATED_AUTHOR
        defaultResourceShouldBeFound("author.doesNotContain=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated equals to DEFAULT_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.equals=" + DEFAULT_LAST_UPDATED);

        // Get all the resourceList where lastUpdated equals to UPDATED_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.equals=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated in DEFAULT_LAST_UPDATED or UPDATED_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.in=" + DEFAULT_LAST_UPDATED + "," + UPDATED_LAST_UPDATED);

        // Get all the resourceList where lastUpdated equals to UPDATED_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.in=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated is not null
        defaultResourceShouldBeFound("lastUpdated.specified=true");

        // Get all the resourceList where lastUpdated is null
        defaultResourceShouldNotBeFound("lastUpdated.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated is greater than or equal to DEFAULT_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.greaterThanOrEqual=" + DEFAULT_LAST_UPDATED);

        // Get all the resourceList where lastUpdated is greater than or equal to UPDATED_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.greaterThanOrEqual=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated is less than or equal to DEFAULT_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.lessThanOrEqual=" + DEFAULT_LAST_UPDATED);

        // Get all the resourceList where lastUpdated is less than or equal to SMALLER_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.lessThanOrEqual=" + SMALLER_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated is less than DEFAULT_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.lessThan=" + DEFAULT_LAST_UPDATED);

        // Get all the resourceList where lastUpdated is less than UPDATED_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.lessThan=" + UPDATED_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByLastUpdatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where lastUpdated is greater than DEFAULT_LAST_UPDATED
        defaultResourceShouldNotBeFound("lastUpdated.greaterThan=" + DEFAULT_LAST_UPDATED);

        // Get all the resourceList where lastUpdated is greater than SMALLER_LAST_UPDATED
        defaultResourceShouldBeFound("lastUpdated.greaterThan=" + SMALLER_LAST_UPDATED);
    }

    @Test
    @Transactional
    void getAllResourcesByActivatedIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where activated equals to DEFAULT_ACTIVATED
        defaultResourceShouldBeFound("activated.equals=" + DEFAULT_ACTIVATED);

        // Get all the resourceList where activated equals to UPDATED_ACTIVATED
        defaultResourceShouldNotBeFound("activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllResourcesByActivatedIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where activated in DEFAULT_ACTIVATED or UPDATED_ACTIVATED
        defaultResourceShouldBeFound("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED);

        // Get all the resourceList where activated equals to UPDATED_ACTIVATED
        defaultResourceShouldNotBeFound("activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllResourcesByActivatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where activated is not null
        defaultResourceShouldBeFound("activated.specified=true");

        // Get all the resourceList where activated is null
        defaultResourceShouldNotBeFound("activated.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views equals to DEFAULT_VIEWS
        defaultResourceShouldBeFound("views.equals=" + DEFAULT_VIEWS);

        // Get all the resourceList where views equals to UPDATED_VIEWS
        defaultResourceShouldNotBeFound("views.equals=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views in DEFAULT_VIEWS or UPDATED_VIEWS
        defaultResourceShouldBeFound("views.in=" + DEFAULT_VIEWS + "," + UPDATED_VIEWS);

        // Get all the resourceList where views equals to UPDATED_VIEWS
        defaultResourceShouldNotBeFound("views.in=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views is not null
        defaultResourceShouldBeFound("views.specified=true");

        // Get all the resourceList where views is null
        defaultResourceShouldNotBeFound("views.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views is greater than or equal to DEFAULT_VIEWS
        defaultResourceShouldBeFound("views.greaterThanOrEqual=" + DEFAULT_VIEWS);

        // Get all the resourceList where views is greater than or equal to UPDATED_VIEWS
        defaultResourceShouldNotBeFound("views.greaterThanOrEqual=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views is less than or equal to DEFAULT_VIEWS
        defaultResourceShouldBeFound("views.lessThanOrEqual=" + DEFAULT_VIEWS);

        // Get all the resourceList where views is less than or equal to SMALLER_VIEWS
        defaultResourceShouldNotBeFound("views.lessThanOrEqual=" + SMALLER_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views is less than DEFAULT_VIEWS
        defaultResourceShouldNotBeFound("views.lessThan=" + DEFAULT_VIEWS);

        // Get all the resourceList where views is less than UPDATED_VIEWS
        defaultResourceShouldBeFound("views.lessThan=" + UPDATED_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByViewsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where views is greater than DEFAULT_VIEWS
        defaultResourceShouldNotBeFound("views.greaterThan=" + DEFAULT_VIEWS);

        // Get all the resourceList where views is greater than SMALLER_VIEWS
        defaultResourceShouldBeFound("views.greaterThan=" + SMALLER_VIEWS);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes equals to DEFAULT_VOTES
        defaultResourceShouldBeFound("votes.equals=" + DEFAULT_VOTES);

        // Get all the resourceList where votes equals to UPDATED_VOTES
        defaultResourceShouldNotBeFound("votes.equals=" + UPDATED_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes in DEFAULT_VOTES or UPDATED_VOTES
        defaultResourceShouldBeFound("votes.in=" + DEFAULT_VOTES + "," + UPDATED_VOTES);

        // Get all the resourceList where votes equals to UPDATED_VOTES
        defaultResourceShouldNotBeFound("votes.in=" + UPDATED_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes is not null
        defaultResourceShouldBeFound("votes.specified=true");

        // Get all the resourceList where votes is null
        defaultResourceShouldNotBeFound("votes.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes is greater than or equal to DEFAULT_VOTES
        defaultResourceShouldBeFound("votes.greaterThanOrEqual=" + DEFAULT_VOTES);

        // Get all the resourceList where votes is greater than or equal to UPDATED_VOTES
        defaultResourceShouldNotBeFound("votes.greaterThanOrEqual=" + UPDATED_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes is less than or equal to DEFAULT_VOTES
        defaultResourceShouldBeFound("votes.lessThanOrEqual=" + DEFAULT_VOTES);

        // Get all the resourceList where votes is less than or equal to SMALLER_VOTES
        defaultResourceShouldNotBeFound("votes.lessThanOrEqual=" + SMALLER_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsLessThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes is less than DEFAULT_VOTES
        defaultResourceShouldNotBeFound("votes.lessThan=" + DEFAULT_VOTES);

        // Get all the resourceList where votes is less than UPDATED_VOTES
        defaultResourceShouldBeFound("votes.lessThan=" + UPDATED_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByVotesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where votes is greater than DEFAULT_VOTES
        defaultResourceShouldNotBeFound("votes.greaterThan=" + DEFAULT_VOTES);

        // Get all the resourceList where votes is greater than SMALLER_VOTES
        defaultResourceShouldBeFound("votes.greaterThan=" + SMALLER_VOTES);
    }

    @Test
    @Transactional
    void getAllResourcesByApprovedByIsEqualToSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where approvedBy equals to DEFAULT_APPROVED_BY
        defaultResourceShouldBeFound("approvedBy.equals=" + DEFAULT_APPROVED_BY);

        // Get all the resourceList where approvedBy equals to UPDATED_APPROVED_BY
        defaultResourceShouldNotBeFound("approvedBy.equals=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllResourcesByApprovedByIsInShouldWork() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where approvedBy in DEFAULT_APPROVED_BY or UPDATED_APPROVED_BY
        defaultResourceShouldBeFound("approvedBy.in=" + DEFAULT_APPROVED_BY + "," + UPDATED_APPROVED_BY);

        // Get all the resourceList where approvedBy equals to UPDATED_APPROVED_BY
        defaultResourceShouldNotBeFound("approvedBy.in=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllResourcesByApprovedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where approvedBy is not null
        defaultResourceShouldBeFound("approvedBy.specified=true");

        // Get all the resourceList where approvedBy is null
        defaultResourceShouldNotBeFound("approvedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllResourcesByApprovedByContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where approvedBy contains DEFAULT_APPROVED_BY
        defaultResourceShouldBeFound("approvedBy.contains=" + DEFAULT_APPROVED_BY);

        // Get all the resourceList where approvedBy contains UPDATED_APPROVED_BY
        defaultResourceShouldNotBeFound("approvedBy.contains=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllResourcesByApprovedByNotContainsSomething() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        // Get all the resourceList where approvedBy does not contain DEFAULT_APPROVED_BY
        defaultResourceShouldNotBeFound("approvedBy.doesNotContain=" + DEFAULT_APPROVED_BY);

        // Get all the resourceList where approvedBy does not contain UPDATED_APPROVED_BY
        defaultResourceShouldBeFound("approvedBy.doesNotContain=" + UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void getAllResourcesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            resourceRepository.saveAndFlush(resource);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        resource.setUser(user);
        resourceRepository.saveAndFlush(resource);
        String userId = user.getId();

        // Get all the resourceList where user equals to userId
        defaultResourceShouldBeFound("userId.equals=" + userId);

        // Get all the resourceList where user equals to "invalid-id"
        defaultResourceShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllResourcesBySubjectIsEqualToSomething() throws Exception {
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            resourceRepository.saveAndFlush(resource);
            subject = SubjectResourceIT.createEntity(em);
        } else {
            subject = TestUtil.findAll(em, Subject.class).get(0);
        }
        em.persist(subject);
        em.flush();
        resource.setSubject(subject);
        resourceRepository.saveAndFlush(resource);
        Long subjectId = subject.getId();

        // Get all the resourceList where subject equals to subjectId
        defaultResourceShouldBeFound("subjectId.equals=" + subjectId);

        // Get all the resourceList where subject equals to (subjectId + 1)
        defaultResourceShouldNotBeFound("subjectId.equals=" + (subjectId + 1));
    }

    @Test
    @Transactional
    void getAllResourcesByTopicsIsEqualToSomething() throws Exception {
        Topic topics;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            resourceRepository.saveAndFlush(resource);
            topics = TopicResourceIT.createEntity(em);
        } else {
            topics = TestUtil.findAll(em, Topic.class).get(0);
        }
        em.persist(topics);
        em.flush();
        resource.addTopics(topics);
        resourceRepository.saveAndFlush(resource);
        Long topicsId = topics.getId();

        // Get all the resourceList where topics equals to topicsId
        defaultResourceShouldBeFound("topicsId.equals=" + topicsId);

        // Get all the resourceList where topics equals to (topicsId + 1)
        defaultResourceShouldNotBeFound("topicsId.equals=" + (topicsId + 1));
    }

    @Test
    @Transactional
    void getAllResourcesBySkillsIsEqualToSomething() throws Exception {
        Skill skills;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            resourceRepository.saveAndFlush(resource);
            skills = SkillResourceIT.createEntity(em);
        } else {
            skills = TestUtil.findAll(em, Skill.class).get(0);
        }
        em.persist(skills);
        em.flush();
        resource.addSkills(skills);
        resourceRepository.saveAndFlush(resource);
        Long skillsId = skills.getId();

        // Get all the resourceList where skills equals to skillsId
        defaultResourceShouldBeFound("skillsId.equals=" + skillsId);

        // Get all the resourceList where skills equals to (skillsId + 1)
        defaultResourceShouldNotBeFound("skillsId.equals=" + (skillsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResourceShouldBeFound(String filter) throws Exception {
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].angeRage").value(hasItem(DEFAULT_ANGE_RAGE.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].votes").value(hasItem(DEFAULT_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)));

        // Check, that the count call also returns 1
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResourceShouldNotBeFound(String filter) throws Exception {
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResourceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResource() throws Exception {
        // Get the resource
        restResourceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        resourceSearchRepository.save(resource);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());

        // Update the resource
        Resource updatedResource = resourceRepository.findById(resource.getId()).get();
        // Disconnect from session so that the updates on updatedResource are not directly saved in db
        em.detach(updatedResource);
        updatedResource
            .title(UPDATED_TITLE)
            .creationDate(UPDATED_CREATION_DATE)
            .description(UPDATED_DESCRIPTION)
            .resourceType(UPDATED_RESOURCE_TYPE)
            .angeRage(UPDATED_ANGE_RAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL)
            .author(UPDATED_AUTHOR)
            .lastUpdated(UPDATED_LAST_UPDATED)
            .activated(UPDATED_ACTIVATED)
            .views(UPDATED_VIEWS)
            .votes(UPDATED_VOTES)
            .approvedBy(UPDATED_APPROVED_BY);
        ResourceDTO resourceDTO = resourceMapper.toDto(updatedResource);

        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testResource.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testResource.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testResource.getResourceType()).isEqualTo(UPDATED_RESOURCE_TYPE);
        assertThat(testResource.getAngeRage()).isEqualTo(UPDATED_ANGE_RAGE);
        assertThat(testResource.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testResource.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testResource.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testResource.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testResource.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testResource.getActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testResource.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testResource.getVotes()).isEqualTo(UPDATED_VOTES);
        assertThat(testResource.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Resource> resourceSearchList = IterableUtils.toList(resourceSearchRepository.findAll());
                Resource testResourceSearch = resourceSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testResourceSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testResourceSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
                assertThat(testResourceSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testResourceSearch.getResourceType()).isEqualTo(UPDATED_RESOURCE_TYPE);
                assertThat(testResourceSearch.getAngeRage()).isEqualTo(UPDATED_ANGE_RAGE);
                assertThat(testResourceSearch.getFile()).isEqualTo(UPDATED_FILE);
                assertThat(testResourceSearch.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
                assertThat(testResourceSearch.getUrl()).isEqualTo(UPDATED_URL);
                assertThat(testResourceSearch.getAuthor()).isEqualTo(UPDATED_AUTHOR);
                assertThat(testResourceSearch.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
                assertThat(testResourceSearch.getActivated()).isEqualTo(UPDATED_ACTIVATED);
                assertThat(testResourceSearch.getViews()).isEqualTo(UPDATED_VIEWS);
                assertThat(testResourceSearch.getVotes()).isEqualTo(UPDATED_VOTES);
                assertThat(testResourceSearch.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
            });
    }

    @Test
    @Transactional
    void putNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, resourceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource
            .creationDate(UPDATED_CREATION_DATE)
            .angeRage(UPDATED_ANGE_RAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL)
            .lastUpdated(UPDATED_LAST_UPDATED)
            .views(UPDATED_VIEWS)
            .approvedBy(UPDATED_APPROVED_BY);

        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResource.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testResource.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testResource.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testResource.getResourceType()).isEqualTo(DEFAULT_RESOURCE_TYPE);
        assertThat(testResource.getAngeRage()).isEqualTo(UPDATED_ANGE_RAGE);
        assertThat(testResource.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testResource.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testResource.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testResource.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testResource.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testResource.getActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testResource.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testResource.getVotes()).isEqualTo(DEFAULT_VOTES);
        assertThat(testResource.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void fullUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);

        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource
            .title(UPDATED_TITLE)
            .creationDate(UPDATED_CREATION_DATE)
            .description(UPDATED_DESCRIPTION)
            .resourceType(UPDATED_RESOURCE_TYPE)
            .angeRage(UPDATED_ANGE_RAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL)
            .author(UPDATED_AUTHOR)
            .lastUpdated(UPDATED_LAST_UPDATED)
            .activated(UPDATED_ACTIVATED)
            .views(UPDATED_VIEWS)
            .votes(UPDATED_VOTES)
            .approvedBy(UPDATED_APPROVED_BY);

        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResource.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            )
            .andExpect(status().isOk());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testResource.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testResource.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testResource.getResourceType()).isEqualTo(UPDATED_RESOURCE_TYPE);
        assertThat(testResource.getAngeRage()).isEqualTo(UPDATED_ANGE_RAGE);
        assertThat(testResource.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testResource.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testResource.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testResource.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testResource.getLastUpdated()).isEqualTo(UPDATED_LAST_UPDATED);
        assertThat(testResource.getActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testResource.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testResource.getVotes()).isEqualTo(UPDATED_VOTES);
        assertThat(testResource.getApprovedBy()).isEqualTo(UPDATED_APPROVED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, resourceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        resource.setId(count.incrementAndGet());

        // Create the Resource
        ResourceDTO resourceDTO = resourceMapper.toDto(resource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResourceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(resourceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteResource() throws Exception {
        // Initialize the database
        resourceRepository.saveAndFlush(resource);
        resourceRepository.save(resource);
        resourceSearchRepository.save(resource);

        int databaseSizeBeforeDelete = resourceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the resource
        restResourceMockMvc
            .perform(delete(ENTITY_API_URL_ID, resource.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Resource> resourceList = resourceRepository.findAll();
        assertThat(resourceList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(resourceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchResource() throws Exception {
        // Initialize the database
        resource = resourceRepository.saveAndFlush(resource);
        resourceSearchRepository.save(resource);

        // Search the resource
        restResourceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + resource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(resource.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].angeRage").value(hasItem(DEFAULT_ANGE_RAGE.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS.intValue())))
            .andExpect(jsonPath("$.[*].votes").value(hasItem(DEFAULT_VOTES.intValue())))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)));
    }
}
