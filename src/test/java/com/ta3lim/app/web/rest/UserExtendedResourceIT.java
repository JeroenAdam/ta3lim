package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.UserExtended;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.CivilStatus;
import com.ta3lim.app.repository.UserExtendedRepository;
import com.ta3lim.app.repository.search.UserExtendedSearchRepository;
import com.ta3lim.app.service.UserExtendedService;
import com.ta3lim.app.service.criteria.UserExtendedCriteria;
import com.ta3lim.app.service.dto.UserExtendedDTO;
import com.ta3lim.app.service.mapper.UserExtendedMapper;
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
 * Integration tests for the {@link UserExtendedResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserExtendedResourceIT {

    private static final LocalDate DEFAULT_LAST_LOGIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_LOGIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_LOGIN = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_ABOUT_ME = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_ME = "BBBBBBBBBB";

    private static final String DEFAULT_OCCUPATION = "AAAAAAAAAA";
    private static final String UPDATED_OCCUPATION = "BBBBBBBBBB";

    private static final String DEFAULT_SOCIAL_MEDIA = "AAAAAAAAAA";
    private static final String UPDATED_SOCIAL_MEDIA = "BBBBBBBBBB";

    private static final CivilStatus DEFAULT_CIVIL_STATUS = CivilStatus.MARRIED;
    private static final CivilStatus UPDATED_CIVIL_STATUS = CivilStatus.DIVORCED;

    private static final Children DEFAULT_FIRSTCHILD = Children.AGE_00_03;
    private static final Children UPDATED_FIRSTCHILD = Children.AGE_04_06;

    private static final Children DEFAULT_SECONDCHILD = Children.AGE_00_03;
    private static final Children UPDATED_SECONDCHILD = Children.AGE_04_06;

    private static final Children DEFAULT_THIRDCHILD = Children.AGE_00_03;
    private static final Children UPDATED_THIRDCHILD = Children.AGE_04_06;

    private static final Children DEFAULT_FOURTHCHILD = Children.AGE_00_03;
    private static final Children UPDATED_FOURTHCHILD = Children.AGE_04_06;

    private static final Integer DEFAULT_FILESQUOTA = 1;
    private static final Integer UPDATED_FILESQUOTA = 2;
    private static final Integer SMALLER_FILESQUOTA = 1 - 1;

    private static final LocalDate DEFAULT_APPROVER_SINCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_APPROVER_SINCE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_APPROVER_SINCE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_LAST_APPROVAL = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_APPROVAL = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_APPROVAL = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/user-extendeds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-extendeds";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserExtendedRepository userExtendedRepository;

    @Mock
    private UserExtendedRepository userExtendedRepositoryMock;

    @Autowired
    private UserExtendedMapper userExtendedMapper;

    @Mock
    private UserExtendedService userExtendedServiceMock;

    @Autowired
    private UserExtendedSearchRepository userExtendedSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserExtendedMockMvc;

    private UserExtended userExtended;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtended createEntity(EntityManager em) {
        UserExtended userExtended = new UserExtended()
            .lastLogin(DEFAULT_LAST_LOGIN)
            .aboutMe(DEFAULT_ABOUT_ME)
            .occupation(DEFAULT_OCCUPATION)
            .socialMedia(DEFAULT_SOCIAL_MEDIA)
            .civilStatus(DEFAULT_CIVIL_STATUS)
            .firstchild(DEFAULT_FIRSTCHILD)
            .secondchild(DEFAULT_SECONDCHILD)
            .thirdchild(DEFAULT_THIRDCHILD)
            .fourthchild(DEFAULT_FOURTHCHILD)
            .filesquota(DEFAULT_FILESQUOTA)
            .approverSince(DEFAULT_APPROVER_SINCE)
            .lastApproval(DEFAULT_LAST_APPROVAL);
        return userExtended;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtended createUpdatedEntity(EntityManager em) {
        UserExtended userExtended = new UserExtended()
            .lastLogin(UPDATED_LAST_LOGIN)
            .aboutMe(UPDATED_ABOUT_ME)
            .occupation(UPDATED_OCCUPATION)
            .socialMedia(UPDATED_SOCIAL_MEDIA)
            .civilStatus(UPDATED_CIVIL_STATUS)
            .firstchild(UPDATED_FIRSTCHILD)
            .secondchild(UPDATED_SECONDCHILD)
            .thirdchild(UPDATED_THIRDCHILD)
            .fourthchild(UPDATED_FOURTHCHILD)
            .filesquota(UPDATED_FILESQUOTA)
            .approverSince(UPDATED_APPROVER_SINCE)
            .lastApproval(UPDATED_LAST_APPROVAL);
        return userExtended;
    }

    @BeforeEach
    public void initTest() {
        userExtendedSearchRepository.deleteAll();
        userExtended = createEntity(em);
    }

    @Test
    @Transactional
    void createUserExtended() throws Exception {
        int databaseSizeBeforeCreate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);
        restUserExtendedMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UserExtended testUserExtended = userExtendedList.get(userExtendedList.size() - 1);
        assertThat(testUserExtended.getLastLogin()).isEqualTo(DEFAULT_LAST_LOGIN);
        assertThat(testUserExtended.getAboutMe()).isEqualTo(DEFAULT_ABOUT_ME);
        assertThat(testUserExtended.getOccupation()).isEqualTo(DEFAULT_OCCUPATION);
        assertThat(testUserExtended.getSocialMedia()).isEqualTo(DEFAULT_SOCIAL_MEDIA);
        assertThat(testUserExtended.getCivilStatus()).isEqualTo(DEFAULT_CIVIL_STATUS);
        assertThat(testUserExtended.getFirstchild()).isEqualTo(DEFAULT_FIRSTCHILD);
        assertThat(testUserExtended.getSecondchild()).isEqualTo(DEFAULT_SECONDCHILD);
        assertThat(testUserExtended.getThirdchild()).isEqualTo(DEFAULT_THIRDCHILD);
        assertThat(testUserExtended.getFourthchild()).isEqualTo(DEFAULT_FOURTHCHILD);
        assertThat(testUserExtended.getFilesquota()).isEqualTo(DEFAULT_FILESQUOTA);
        assertThat(testUserExtended.getApproverSince()).isEqualTo(DEFAULT_APPROVER_SINCE);
        assertThat(testUserExtended.getLastApproval()).isEqualTo(DEFAULT_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void createUserExtendedWithExistingId() throws Exception {
        // Create the UserExtended with an existing ID
        userExtended.setId(1L);
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        int databaseSizeBeforeCreate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserExtendedMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUserExtendeds() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExtended.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].aboutMe").value(hasItem(DEFAULT_ABOUT_ME)))
            .andExpect(jsonPath("$.[*].occupation").value(hasItem(DEFAULT_OCCUPATION)))
            .andExpect(jsonPath("$.[*].socialMedia").value(hasItem(DEFAULT_SOCIAL_MEDIA)))
            .andExpect(jsonPath("$.[*].civilStatus").value(hasItem(DEFAULT_CIVIL_STATUS.toString())))
            .andExpect(jsonPath("$.[*].firstchild").value(hasItem(DEFAULT_FIRSTCHILD.toString())))
            .andExpect(jsonPath("$.[*].secondchild").value(hasItem(DEFAULT_SECONDCHILD.toString())))
            .andExpect(jsonPath("$.[*].thirdchild").value(hasItem(DEFAULT_THIRDCHILD.toString())))
            .andExpect(jsonPath("$.[*].fourthchild").value(hasItem(DEFAULT_FOURTHCHILD.toString())))
            .andExpect(jsonPath("$.[*].filesquota").value(hasItem(DEFAULT_FILESQUOTA)))
            .andExpect(jsonPath("$.[*].approverSince").value(hasItem(DEFAULT_APPROVER_SINCE.toString())))
            .andExpect(jsonPath("$.[*].lastApproval").value(hasItem(DEFAULT_LAST_APPROVAL.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExtendedsWithEagerRelationshipsIsEnabled() throws Exception {
        when(userExtendedServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExtendedMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userExtendedServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserExtendedsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userExtendedServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserExtendedMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userExtendedRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserExtended() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get the userExtended
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL_ID, userExtended.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userExtended.getId().intValue()))
            .andExpect(jsonPath("$.lastLogin").value(DEFAULT_LAST_LOGIN.toString()))
            .andExpect(jsonPath("$.aboutMe").value(DEFAULT_ABOUT_ME))
            .andExpect(jsonPath("$.occupation").value(DEFAULT_OCCUPATION))
            .andExpect(jsonPath("$.socialMedia").value(DEFAULT_SOCIAL_MEDIA))
            .andExpect(jsonPath("$.civilStatus").value(DEFAULT_CIVIL_STATUS.toString()))
            .andExpect(jsonPath("$.firstchild").value(DEFAULT_FIRSTCHILD.toString()))
            .andExpect(jsonPath("$.secondchild").value(DEFAULT_SECONDCHILD.toString()))
            .andExpect(jsonPath("$.thirdchild").value(DEFAULT_THIRDCHILD.toString()))
            .andExpect(jsonPath("$.fourthchild").value(DEFAULT_FOURTHCHILD.toString()))
            .andExpect(jsonPath("$.filesquota").value(DEFAULT_FILESQUOTA))
            .andExpect(jsonPath("$.approverSince").value(DEFAULT_APPROVER_SINCE.toString()))
            .andExpect(jsonPath("$.lastApproval").value(DEFAULT_LAST_APPROVAL.toString()));
    }

    @Test
    @Transactional
    void getUserExtendedsByIdFiltering() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        Long id = userExtended.getId();

        defaultUserExtendedShouldBeFound("id.equals=" + id);
        defaultUserExtendedShouldNotBeFound("id.notEquals=" + id);

        defaultUserExtendedShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserExtendedShouldNotBeFound("id.greaterThan=" + id);

        defaultUserExtendedShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserExtendedShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin equals to DEFAULT_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.equals=" + DEFAULT_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin equals to UPDATED_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.equals=" + UPDATED_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin in DEFAULT_LAST_LOGIN or UPDATED_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.in=" + DEFAULT_LAST_LOGIN + "," + UPDATED_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin equals to UPDATED_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.in=" + UPDATED_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin is not null
        defaultUserExtendedShouldBeFound("lastLogin.specified=true");

        // Get all the userExtendedList where lastLogin is null
        defaultUserExtendedShouldNotBeFound("lastLogin.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin is greater than or equal to DEFAULT_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.greaterThanOrEqual=" + DEFAULT_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin is greater than or equal to UPDATED_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.greaterThanOrEqual=" + UPDATED_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin is less than or equal to DEFAULT_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.lessThanOrEqual=" + DEFAULT_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin is less than or equal to SMALLER_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.lessThanOrEqual=" + SMALLER_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsLessThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin is less than DEFAULT_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.lessThan=" + DEFAULT_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin is less than UPDATED_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.lessThan=" + UPDATED_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastLoginIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastLogin is greater than DEFAULT_LAST_LOGIN
        defaultUserExtendedShouldNotBeFound("lastLogin.greaterThan=" + DEFAULT_LAST_LOGIN);

        // Get all the userExtendedList where lastLogin is greater than SMALLER_LAST_LOGIN
        defaultUserExtendedShouldBeFound("lastLogin.greaterThan=" + SMALLER_LAST_LOGIN);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByAboutMeIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where aboutMe equals to DEFAULT_ABOUT_ME
        defaultUserExtendedShouldBeFound("aboutMe.equals=" + DEFAULT_ABOUT_ME);

        // Get all the userExtendedList where aboutMe equals to UPDATED_ABOUT_ME
        defaultUserExtendedShouldNotBeFound("aboutMe.equals=" + UPDATED_ABOUT_ME);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByAboutMeIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where aboutMe in DEFAULT_ABOUT_ME or UPDATED_ABOUT_ME
        defaultUserExtendedShouldBeFound("aboutMe.in=" + DEFAULT_ABOUT_ME + "," + UPDATED_ABOUT_ME);

        // Get all the userExtendedList where aboutMe equals to UPDATED_ABOUT_ME
        defaultUserExtendedShouldNotBeFound("aboutMe.in=" + UPDATED_ABOUT_ME);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByAboutMeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where aboutMe is not null
        defaultUserExtendedShouldBeFound("aboutMe.specified=true");

        // Get all the userExtendedList where aboutMe is null
        defaultUserExtendedShouldNotBeFound("aboutMe.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByAboutMeContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where aboutMe contains DEFAULT_ABOUT_ME
        defaultUserExtendedShouldBeFound("aboutMe.contains=" + DEFAULT_ABOUT_ME);

        // Get all the userExtendedList where aboutMe contains UPDATED_ABOUT_ME
        defaultUserExtendedShouldNotBeFound("aboutMe.contains=" + UPDATED_ABOUT_ME);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByAboutMeNotContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where aboutMe does not contain DEFAULT_ABOUT_ME
        defaultUserExtendedShouldNotBeFound("aboutMe.doesNotContain=" + DEFAULT_ABOUT_ME);

        // Get all the userExtendedList where aboutMe does not contain UPDATED_ABOUT_ME
        defaultUserExtendedShouldBeFound("aboutMe.doesNotContain=" + UPDATED_ABOUT_ME);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByOccupationIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where occupation equals to DEFAULT_OCCUPATION
        defaultUserExtendedShouldBeFound("occupation.equals=" + DEFAULT_OCCUPATION);

        // Get all the userExtendedList where occupation equals to UPDATED_OCCUPATION
        defaultUserExtendedShouldNotBeFound("occupation.equals=" + UPDATED_OCCUPATION);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByOccupationIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where occupation in DEFAULT_OCCUPATION or UPDATED_OCCUPATION
        defaultUserExtendedShouldBeFound("occupation.in=" + DEFAULT_OCCUPATION + "," + UPDATED_OCCUPATION);

        // Get all the userExtendedList where occupation equals to UPDATED_OCCUPATION
        defaultUserExtendedShouldNotBeFound("occupation.in=" + UPDATED_OCCUPATION);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByOccupationIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where occupation is not null
        defaultUserExtendedShouldBeFound("occupation.specified=true");

        // Get all the userExtendedList where occupation is null
        defaultUserExtendedShouldNotBeFound("occupation.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByOccupationContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where occupation contains DEFAULT_OCCUPATION
        defaultUserExtendedShouldBeFound("occupation.contains=" + DEFAULT_OCCUPATION);

        // Get all the userExtendedList where occupation contains UPDATED_OCCUPATION
        defaultUserExtendedShouldNotBeFound("occupation.contains=" + UPDATED_OCCUPATION);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByOccupationNotContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where occupation does not contain DEFAULT_OCCUPATION
        defaultUserExtendedShouldNotBeFound("occupation.doesNotContain=" + DEFAULT_OCCUPATION);

        // Get all the userExtendedList where occupation does not contain UPDATED_OCCUPATION
        defaultUserExtendedShouldBeFound("occupation.doesNotContain=" + UPDATED_OCCUPATION);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySocialMediaIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where socialMedia equals to DEFAULT_SOCIAL_MEDIA
        defaultUserExtendedShouldBeFound("socialMedia.equals=" + DEFAULT_SOCIAL_MEDIA);

        // Get all the userExtendedList where socialMedia equals to UPDATED_SOCIAL_MEDIA
        defaultUserExtendedShouldNotBeFound("socialMedia.equals=" + UPDATED_SOCIAL_MEDIA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySocialMediaIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where socialMedia in DEFAULT_SOCIAL_MEDIA or UPDATED_SOCIAL_MEDIA
        defaultUserExtendedShouldBeFound("socialMedia.in=" + DEFAULT_SOCIAL_MEDIA + "," + UPDATED_SOCIAL_MEDIA);

        // Get all the userExtendedList where socialMedia equals to UPDATED_SOCIAL_MEDIA
        defaultUserExtendedShouldNotBeFound("socialMedia.in=" + UPDATED_SOCIAL_MEDIA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySocialMediaIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where socialMedia is not null
        defaultUserExtendedShouldBeFound("socialMedia.specified=true");

        // Get all the userExtendedList where socialMedia is null
        defaultUserExtendedShouldNotBeFound("socialMedia.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySocialMediaContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where socialMedia contains DEFAULT_SOCIAL_MEDIA
        defaultUserExtendedShouldBeFound("socialMedia.contains=" + DEFAULT_SOCIAL_MEDIA);

        // Get all the userExtendedList where socialMedia contains UPDATED_SOCIAL_MEDIA
        defaultUserExtendedShouldNotBeFound("socialMedia.contains=" + UPDATED_SOCIAL_MEDIA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySocialMediaNotContainsSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where socialMedia does not contain DEFAULT_SOCIAL_MEDIA
        defaultUserExtendedShouldNotBeFound("socialMedia.doesNotContain=" + DEFAULT_SOCIAL_MEDIA);

        // Get all the userExtendedList where socialMedia does not contain UPDATED_SOCIAL_MEDIA
        defaultUserExtendedShouldBeFound("socialMedia.doesNotContain=" + UPDATED_SOCIAL_MEDIA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByCivilStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where civilStatus equals to DEFAULT_CIVIL_STATUS
        defaultUserExtendedShouldBeFound("civilStatus.equals=" + DEFAULT_CIVIL_STATUS);

        // Get all the userExtendedList where civilStatus equals to UPDATED_CIVIL_STATUS
        defaultUserExtendedShouldNotBeFound("civilStatus.equals=" + UPDATED_CIVIL_STATUS);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByCivilStatusIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where civilStatus in DEFAULT_CIVIL_STATUS or UPDATED_CIVIL_STATUS
        defaultUserExtendedShouldBeFound("civilStatus.in=" + DEFAULT_CIVIL_STATUS + "," + UPDATED_CIVIL_STATUS);

        // Get all the userExtendedList where civilStatus equals to UPDATED_CIVIL_STATUS
        defaultUserExtendedShouldNotBeFound("civilStatus.in=" + UPDATED_CIVIL_STATUS);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByCivilStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where civilStatus is not null
        defaultUserExtendedShouldBeFound("civilStatus.specified=true");

        // Get all the userExtendedList where civilStatus is null
        defaultUserExtendedShouldNotBeFound("civilStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFirstchildIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where firstchild equals to DEFAULT_FIRSTCHILD
        defaultUserExtendedShouldBeFound("firstchild.equals=" + DEFAULT_FIRSTCHILD);

        // Get all the userExtendedList where firstchild equals to UPDATED_FIRSTCHILD
        defaultUserExtendedShouldNotBeFound("firstchild.equals=" + UPDATED_FIRSTCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFirstchildIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where firstchild in DEFAULT_FIRSTCHILD or UPDATED_FIRSTCHILD
        defaultUserExtendedShouldBeFound("firstchild.in=" + DEFAULT_FIRSTCHILD + "," + UPDATED_FIRSTCHILD);

        // Get all the userExtendedList where firstchild equals to UPDATED_FIRSTCHILD
        defaultUserExtendedShouldNotBeFound("firstchild.in=" + UPDATED_FIRSTCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFirstchildIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where firstchild is not null
        defaultUserExtendedShouldBeFound("firstchild.specified=true");

        // Get all the userExtendedList where firstchild is null
        defaultUserExtendedShouldNotBeFound("firstchild.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySecondchildIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where secondchild equals to DEFAULT_SECONDCHILD
        defaultUserExtendedShouldBeFound("secondchild.equals=" + DEFAULT_SECONDCHILD);

        // Get all the userExtendedList where secondchild equals to UPDATED_SECONDCHILD
        defaultUserExtendedShouldNotBeFound("secondchild.equals=" + UPDATED_SECONDCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySecondchildIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where secondchild in DEFAULT_SECONDCHILD or UPDATED_SECONDCHILD
        defaultUserExtendedShouldBeFound("secondchild.in=" + DEFAULT_SECONDCHILD + "," + UPDATED_SECONDCHILD);

        // Get all the userExtendedList where secondchild equals to UPDATED_SECONDCHILD
        defaultUserExtendedShouldNotBeFound("secondchild.in=" + UPDATED_SECONDCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsBySecondchildIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where secondchild is not null
        defaultUserExtendedShouldBeFound("secondchild.specified=true");

        // Get all the userExtendedList where secondchild is null
        defaultUserExtendedShouldNotBeFound("secondchild.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByThirdchildIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where thirdchild equals to DEFAULT_THIRDCHILD
        defaultUserExtendedShouldBeFound("thirdchild.equals=" + DEFAULT_THIRDCHILD);

        // Get all the userExtendedList where thirdchild equals to UPDATED_THIRDCHILD
        defaultUserExtendedShouldNotBeFound("thirdchild.equals=" + UPDATED_THIRDCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByThirdchildIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where thirdchild in DEFAULT_THIRDCHILD or UPDATED_THIRDCHILD
        defaultUserExtendedShouldBeFound("thirdchild.in=" + DEFAULT_THIRDCHILD + "," + UPDATED_THIRDCHILD);

        // Get all the userExtendedList where thirdchild equals to UPDATED_THIRDCHILD
        defaultUserExtendedShouldNotBeFound("thirdchild.in=" + UPDATED_THIRDCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByThirdchildIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where thirdchild is not null
        defaultUserExtendedShouldBeFound("thirdchild.specified=true");

        // Get all the userExtendedList where thirdchild is null
        defaultUserExtendedShouldNotBeFound("thirdchild.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFourthchildIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where fourthchild equals to DEFAULT_FOURTHCHILD
        defaultUserExtendedShouldBeFound("fourthchild.equals=" + DEFAULT_FOURTHCHILD);

        // Get all the userExtendedList where fourthchild equals to UPDATED_FOURTHCHILD
        defaultUserExtendedShouldNotBeFound("fourthchild.equals=" + UPDATED_FOURTHCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFourthchildIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where fourthchild in DEFAULT_FOURTHCHILD or UPDATED_FOURTHCHILD
        defaultUserExtendedShouldBeFound("fourthchild.in=" + DEFAULT_FOURTHCHILD + "," + UPDATED_FOURTHCHILD);

        // Get all the userExtendedList where fourthchild equals to UPDATED_FOURTHCHILD
        defaultUserExtendedShouldNotBeFound("fourthchild.in=" + UPDATED_FOURTHCHILD);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFourthchildIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where fourthchild is not null
        defaultUserExtendedShouldBeFound("fourthchild.specified=true");

        // Get all the userExtendedList where fourthchild is null
        defaultUserExtendedShouldNotBeFound("fourthchild.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota equals to DEFAULT_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.equals=" + DEFAULT_FILESQUOTA);

        // Get all the userExtendedList where filesquota equals to UPDATED_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.equals=" + UPDATED_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota in DEFAULT_FILESQUOTA or UPDATED_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.in=" + DEFAULT_FILESQUOTA + "," + UPDATED_FILESQUOTA);

        // Get all the userExtendedList where filesquota equals to UPDATED_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.in=" + UPDATED_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota is not null
        defaultUserExtendedShouldBeFound("filesquota.specified=true");

        // Get all the userExtendedList where filesquota is null
        defaultUserExtendedShouldNotBeFound("filesquota.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota is greater than or equal to DEFAULT_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.greaterThanOrEqual=" + DEFAULT_FILESQUOTA);

        // Get all the userExtendedList where filesquota is greater than or equal to UPDATED_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.greaterThanOrEqual=" + UPDATED_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota is less than or equal to DEFAULT_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.lessThanOrEqual=" + DEFAULT_FILESQUOTA);

        // Get all the userExtendedList where filesquota is less than or equal to SMALLER_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.lessThanOrEqual=" + SMALLER_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsLessThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota is less than DEFAULT_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.lessThan=" + DEFAULT_FILESQUOTA);

        // Get all the userExtendedList where filesquota is less than UPDATED_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.lessThan=" + UPDATED_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByFilesquotaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where filesquota is greater than DEFAULT_FILESQUOTA
        defaultUserExtendedShouldNotBeFound("filesquota.greaterThan=" + DEFAULT_FILESQUOTA);

        // Get all the userExtendedList where filesquota is greater than SMALLER_FILESQUOTA
        defaultUserExtendedShouldBeFound("filesquota.greaterThan=" + SMALLER_FILESQUOTA);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince equals to DEFAULT_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.equals=" + DEFAULT_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince equals to UPDATED_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.equals=" + UPDATED_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince in DEFAULT_APPROVER_SINCE or UPDATED_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.in=" + DEFAULT_APPROVER_SINCE + "," + UPDATED_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince equals to UPDATED_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.in=" + UPDATED_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince is not null
        defaultUserExtendedShouldBeFound("approverSince.specified=true");

        // Get all the userExtendedList where approverSince is null
        defaultUserExtendedShouldNotBeFound("approverSince.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince is greater than or equal to DEFAULT_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.greaterThanOrEqual=" + DEFAULT_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince is greater than or equal to UPDATED_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.greaterThanOrEqual=" + UPDATED_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince is less than or equal to DEFAULT_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.lessThanOrEqual=" + DEFAULT_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince is less than or equal to SMALLER_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.lessThanOrEqual=" + SMALLER_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsLessThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince is less than DEFAULT_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.lessThan=" + DEFAULT_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince is less than UPDATED_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.lessThan=" + UPDATED_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByApproverSinceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where approverSince is greater than DEFAULT_APPROVER_SINCE
        defaultUserExtendedShouldNotBeFound("approverSince.greaterThan=" + DEFAULT_APPROVER_SINCE);

        // Get all the userExtendedList where approverSince is greater than SMALLER_APPROVER_SINCE
        defaultUserExtendedShouldBeFound("approverSince.greaterThan=" + SMALLER_APPROVER_SINCE);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval equals to DEFAULT_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.equals=" + DEFAULT_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval equals to UPDATED_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.equals=" + UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsInShouldWork() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval in DEFAULT_LAST_APPROVAL or UPDATED_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.in=" + DEFAULT_LAST_APPROVAL + "," + UPDATED_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval equals to UPDATED_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.in=" + UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsNullOrNotNull() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval is not null
        defaultUserExtendedShouldBeFound("lastApproval.specified=true");

        // Get all the userExtendedList where lastApproval is null
        defaultUserExtendedShouldNotBeFound("lastApproval.specified=false");
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval is greater than or equal to DEFAULT_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.greaterThanOrEqual=" + DEFAULT_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval is greater than or equal to UPDATED_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.greaterThanOrEqual=" + UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval is less than or equal to DEFAULT_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.lessThanOrEqual=" + DEFAULT_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval is less than or equal to SMALLER_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.lessThanOrEqual=" + SMALLER_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsLessThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval is less than DEFAULT_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.lessThan=" + DEFAULT_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval is less than UPDATED_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.lessThan=" + UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByLastApprovalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        // Get all the userExtendedList where lastApproval is greater than DEFAULT_LAST_APPROVAL
        defaultUserExtendedShouldNotBeFound("lastApproval.greaterThan=" + DEFAULT_LAST_APPROVAL);

        // Get all the userExtendedList where lastApproval is greater than SMALLER_LAST_APPROVAL
        defaultUserExtendedShouldBeFound("lastApproval.greaterThan=" + SMALLER_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void getAllUserExtendedsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userExtendedRepository.saveAndFlush(userExtended);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        userExtended.setUser(user);
        userExtendedRepository.saveAndFlush(userExtended);
        String userId = user.getId();

        // Get all the userExtendedList where user equals to userId
        defaultUserExtendedShouldBeFound("userId.equals=" + userId);

        // Get all the userExtendedList where user equals to "invalid-id"
        defaultUserExtendedShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserExtendedShouldBeFound(String filter) throws Exception {
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExtended.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].aboutMe").value(hasItem(DEFAULT_ABOUT_ME)))
            .andExpect(jsonPath("$.[*].occupation").value(hasItem(DEFAULT_OCCUPATION)))
            .andExpect(jsonPath("$.[*].socialMedia").value(hasItem(DEFAULT_SOCIAL_MEDIA)))
            .andExpect(jsonPath("$.[*].civilStatus").value(hasItem(DEFAULT_CIVIL_STATUS.toString())))
            .andExpect(jsonPath("$.[*].firstchild").value(hasItem(DEFAULT_FIRSTCHILD.toString())))
            .andExpect(jsonPath("$.[*].secondchild").value(hasItem(DEFAULT_SECONDCHILD.toString())))
            .andExpect(jsonPath("$.[*].thirdchild").value(hasItem(DEFAULT_THIRDCHILD.toString())))
            .andExpect(jsonPath("$.[*].fourthchild").value(hasItem(DEFAULT_FOURTHCHILD.toString())))
            .andExpect(jsonPath("$.[*].filesquota").value(hasItem(DEFAULT_FILESQUOTA)))
            .andExpect(jsonPath("$.[*].approverSince").value(hasItem(DEFAULT_APPROVER_SINCE.toString())))
            .andExpect(jsonPath("$.[*].lastApproval").value(hasItem(DEFAULT_LAST_APPROVAL.toString())));

        // Check, that the count call also returns 1
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserExtendedShouldNotBeFound(String filter) throws Exception {
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserExtendedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserExtended() throws Exception {
        // Get the userExtended
        restUserExtendedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserExtended() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        userExtendedSearchRepository.save(userExtended);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());

        // Update the userExtended
        UserExtended updatedUserExtended = userExtendedRepository.findById(userExtended.getId()).get();
        // Disconnect from session so that the updates on updatedUserExtended are not directly saved in db
        em.detach(updatedUserExtended);
        updatedUserExtended
            .lastLogin(UPDATED_LAST_LOGIN)
            .aboutMe(UPDATED_ABOUT_ME)
            .occupation(UPDATED_OCCUPATION)
            .socialMedia(UPDATED_SOCIAL_MEDIA)
            .civilStatus(UPDATED_CIVIL_STATUS)
            .firstchild(UPDATED_FIRSTCHILD)
            .secondchild(UPDATED_SECONDCHILD)
            .thirdchild(UPDATED_THIRDCHILD)
            .fourthchild(UPDATED_FOURTHCHILD)
            .filesquota(UPDATED_FILESQUOTA)
            .approverSince(UPDATED_APPROVER_SINCE)
            .lastApproval(UPDATED_LAST_APPROVAL);
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(updatedUserExtended);

        restUserExtendedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userExtendedDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        UserExtended testUserExtended = userExtendedList.get(userExtendedList.size() - 1);
        assertThat(testUserExtended.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
        assertThat(testUserExtended.getAboutMe()).isEqualTo(UPDATED_ABOUT_ME);
        assertThat(testUserExtended.getOccupation()).isEqualTo(UPDATED_OCCUPATION);
        assertThat(testUserExtended.getSocialMedia()).isEqualTo(UPDATED_SOCIAL_MEDIA);
        assertThat(testUserExtended.getCivilStatus()).isEqualTo(UPDATED_CIVIL_STATUS);
        assertThat(testUserExtended.getFirstchild()).isEqualTo(UPDATED_FIRSTCHILD);
        assertThat(testUserExtended.getSecondchild()).isEqualTo(UPDATED_SECONDCHILD);
        assertThat(testUserExtended.getThirdchild()).isEqualTo(UPDATED_THIRDCHILD);
        assertThat(testUserExtended.getFourthchild()).isEqualTo(UPDATED_FOURTHCHILD);
        assertThat(testUserExtended.getFilesquota()).isEqualTo(UPDATED_FILESQUOTA);
        assertThat(testUserExtended.getApproverSince()).isEqualTo(UPDATED_APPROVER_SINCE);
        assertThat(testUserExtended.getLastApproval()).isEqualTo(UPDATED_LAST_APPROVAL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserExtended> userExtendedSearchList = IterableUtils.toList(userExtendedSearchRepository.findAll());
                UserExtended testUserExtendedSearch = userExtendedSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUserExtendedSearch.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
                assertThat(testUserExtendedSearch.getAboutMe()).isEqualTo(UPDATED_ABOUT_ME);
                assertThat(testUserExtendedSearch.getOccupation()).isEqualTo(UPDATED_OCCUPATION);
                assertThat(testUserExtendedSearch.getSocialMedia()).isEqualTo(UPDATED_SOCIAL_MEDIA);
                assertThat(testUserExtendedSearch.getCivilStatus()).isEqualTo(UPDATED_CIVIL_STATUS);
                assertThat(testUserExtendedSearch.getFirstchild()).isEqualTo(UPDATED_FIRSTCHILD);
                assertThat(testUserExtendedSearch.getSecondchild()).isEqualTo(UPDATED_SECONDCHILD);
                assertThat(testUserExtendedSearch.getThirdchild()).isEqualTo(UPDATED_THIRDCHILD);
                assertThat(testUserExtendedSearch.getFourthchild()).isEqualTo(UPDATED_FOURTHCHILD);
                assertThat(testUserExtendedSearch.getFilesquota()).isEqualTo(UPDATED_FILESQUOTA);
                assertThat(testUserExtendedSearch.getApproverSince()).isEqualTo(UPDATED_APPROVER_SINCE);
                assertThat(testUserExtendedSearch.getLastApproval()).isEqualTo(UPDATED_LAST_APPROVAL);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userExtendedDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUserExtendedWithPatch() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();

        // Update the userExtended using partial update
        UserExtended partialUpdatedUserExtended = new UserExtended();
        partialUpdatedUserExtended.setId(userExtended.getId());

        partialUpdatedUserExtended
            .occupation(UPDATED_OCCUPATION)
            .secondchild(UPDATED_SECONDCHILD)
            .thirdchild(UPDATED_THIRDCHILD)
            .lastApproval(UPDATED_LAST_APPROVAL);

        restUserExtendedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtended.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtended))
            )
            .andExpect(status().isOk());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        UserExtended testUserExtended = userExtendedList.get(userExtendedList.size() - 1);
        assertThat(testUserExtended.getLastLogin()).isEqualTo(DEFAULT_LAST_LOGIN);
        assertThat(testUserExtended.getAboutMe()).isEqualTo(DEFAULT_ABOUT_ME);
        assertThat(testUserExtended.getOccupation()).isEqualTo(UPDATED_OCCUPATION);
        assertThat(testUserExtended.getSocialMedia()).isEqualTo(DEFAULT_SOCIAL_MEDIA);
        assertThat(testUserExtended.getCivilStatus()).isEqualTo(DEFAULT_CIVIL_STATUS);
        assertThat(testUserExtended.getFirstchild()).isEqualTo(DEFAULT_FIRSTCHILD);
        assertThat(testUserExtended.getSecondchild()).isEqualTo(UPDATED_SECONDCHILD);
        assertThat(testUserExtended.getThirdchild()).isEqualTo(UPDATED_THIRDCHILD);
        assertThat(testUserExtended.getFourthchild()).isEqualTo(DEFAULT_FOURTHCHILD);
        assertThat(testUserExtended.getFilesquota()).isEqualTo(DEFAULT_FILESQUOTA);
        assertThat(testUserExtended.getApproverSince()).isEqualTo(DEFAULT_APPROVER_SINCE);
        assertThat(testUserExtended.getLastApproval()).isEqualTo(UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void fullUpdateUserExtendedWithPatch() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);

        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();

        // Update the userExtended using partial update
        UserExtended partialUpdatedUserExtended = new UserExtended();
        partialUpdatedUserExtended.setId(userExtended.getId());

        partialUpdatedUserExtended
            .lastLogin(UPDATED_LAST_LOGIN)
            .aboutMe(UPDATED_ABOUT_ME)
            .occupation(UPDATED_OCCUPATION)
            .socialMedia(UPDATED_SOCIAL_MEDIA)
            .civilStatus(UPDATED_CIVIL_STATUS)
            .firstchild(UPDATED_FIRSTCHILD)
            .secondchild(UPDATED_SECONDCHILD)
            .thirdchild(UPDATED_THIRDCHILD)
            .fourthchild(UPDATED_FOURTHCHILD)
            .filesquota(UPDATED_FILESQUOTA)
            .approverSince(UPDATED_APPROVER_SINCE)
            .lastApproval(UPDATED_LAST_APPROVAL);

        restUserExtendedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtended.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtended))
            )
            .andExpect(status().isOk());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        UserExtended testUserExtended = userExtendedList.get(userExtendedList.size() - 1);
        assertThat(testUserExtended.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
        assertThat(testUserExtended.getAboutMe()).isEqualTo(UPDATED_ABOUT_ME);
        assertThat(testUserExtended.getOccupation()).isEqualTo(UPDATED_OCCUPATION);
        assertThat(testUserExtended.getSocialMedia()).isEqualTo(UPDATED_SOCIAL_MEDIA);
        assertThat(testUserExtended.getCivilStatus()).isEqualTo(UPDATED_CIVIL_STATUS);
        assertThat(testUserExtended.getFirstchild()).isEqualTo(UPDATED_FIRSTCHILD);
        assertThat(testUserExtended.getSecondchild()).isEqualTo(UPDATED_SECONDCHILD);
        assertThat(testUserExtended.getThirdchild()).isEqualTo(UPDATED_THIRDCHILD);
        assertThat(testUserExtended.getFourthchild()).isEqualTo(UPDATED_FOURTHCHILD);
        assertThat(testUserExtended.getFilesquota()).isEqualTo(UPDATED_FILESQUOTA);
        assertThat(testUserExtended.getApproverSince()).isEqualTo(UPDATED_APPROVER_SINCE);
        assertThat(testUserExtended.getLastApproval()).isEqualTo(UPDATED_LAST_APPROVAL);
    }

    @Test
    @Transactional
    void patchNonExistingUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userExtendedDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserExtended() throws Exception {
        int databaseSizeBeforeUpdate = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        userExtended.setId(count.incrementAndGet());

        // Create the UserExtended
        UserExtendedDTO userExtendedDTO = userExtendedMapper.toDto(userExtended);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtendedMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtendedDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtended in the database
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserExtended() throws Exception {
        // Initialize the database
        userExtendedRepository.saveAndFlush(userExtended);
        userExtendedRepository.save(userExtended);
        userExtendedSearchRepository.save(userExtended);

        int databaseSizeBeforeDelete = userExtendedRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userExtended
        restUserExtendedMockMvc
            .perform(delete(ENTITY_API_URL_ID, userExtended.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserExtended> userExtendedList = userExtendedRepository.findAll();
        assertThat(userExtendedList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userExtendedSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserExtended() throws Exception {
        // Initialize the database
        userExtended = userExtendedRepository.saveAndFlush(userExtended);
        userExtendedSearchRepository.save(userExtended);

        // Search the userExtended
        restUserExtendedMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userExtended.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExtended.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].aboutMe").value(hasItem(DEFAULT_ABOUT_ME)))
            .andExpect(jsonPath("$.[*].occupation").value(hasItem(DEFAULT_OCCUPATION)))
            .andExpect(jsonPath("$.[*].socialMedia").value(hasItem(DEFAULT_SOCIAL_MEDIA)))
            .andExpect(jsonPath("$.[*].civilStatus").value(hasItem(DEFAULT_CIVIL_STATUS.toString())))
            .andExpect(jsonPath("$.[*].firstchild").value(hasItem(DEFAULT_FIRSTCHILD.toString())))
            .andExpect(jsonPath("$.[*].secondchild").value(hasItem(DEFAULT_SECONDCHILD.toString())))
            .andExpect(jsonPath("$.[*].thirdchild").value(hasItem(DEFAULT_THIRDCHILD.toString())))
            .andExpect(jsonPath("$.[*].fourthchild").value(hasItem(DEFAULT_FOURTHCHILD.toString())))
            .andExpect(jsonPath("$.[*].filesquota").value(hasItem(DEFAULT_FILESQUOTA)))
            .andExpect(jsonPath("$.[*].approverSince").value(hasItem(DEFAULT_APPROVER_SINCE.toString())))
            .andExpect(jsonPath("$.[*].lastApproval").value(hasItem(DEFAULT_LAST_APPROVAL.toString())));
    }
}
