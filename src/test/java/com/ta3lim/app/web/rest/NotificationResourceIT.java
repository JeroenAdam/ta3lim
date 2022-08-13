package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.Notification;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.enumeration.NotificationType;
import com.ta3lim.app.repository.NotificationRepository;
import com.ta3lim.app.repository.search.NotificationSearchRepository;
import com.ta3lim.app.service.NotificationService;
import com.ta3lim.app.service.criteria.NotificationCriteria;
import com.ta3lim.app.service.dto.NotificationDTO;
import com.ta3lim.app.service.mapper.NotificationMapper;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_NOTIFICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_NOTIFICATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_NOTIFICATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.UNREAD_MESSAGES;
    private static final NotificationType UPDATED_NOTIFICATION_TYPE = NotificationType.ACCEPTED;

    private static final String DEFAULT_NOTIFICATION_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_NOTIFICATION_TEXT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELIVERED = false;
    private static final Boolean UPDATED_IS_DELIVERED = true;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notifications";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    @Autowired
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationService notificationServiceMock;

    @Autowired
    private NotificationSearchRepository notificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .creationDate(DEFAULT_CREATION_DATE)
            .notificationDate(DEFAULT_NOTIFICATION_DATE)
            .notificationType(DEFAULT_NOTIFICATION_TYPE)
            .notificationText(DEFAULT_NOTIFICATION_TEXT)
            .isDelivered(DEFAULT_IS_DELIVERED)
            .isDeleted(DEFAULT_IS_DELETED);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .creationDate(UPDATED_CREATION_DATE)
            .notificationDate(UPDATED_NOTIFICATION_DATE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationText(UPDATED_NOTIFICATION_TEXT)
            .isDelivered(UPDATED_IS_DELIVERED)
            .isDeleted(UPDATED_IS_DELETED);
        return notification;
    }

    @BeforeEach
    public void initTest() {
        notificationSearchRepository.deleteAll();
        notification = createEntity(em);
    }

    @Test
    @Transactional
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testNotification.getNotificationDate()).isEqualTo(DEFAULT_NOTIFICATION_DATE);
        assertThat(testNotification.getNotificationType()).isEqualTo(DEFAULT_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationText()).isEqualTo(DEFAULT_NOTIFICATION_TEXT);
        assertThat(testNotification.getIsDelivered()).isEqualTo(DEFAULT_IS_DELIVERED);
        assertThat(testNotification.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
    }

    @Test
    @Transactional
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationDate").value(hasItem(DEFAULT_NOTIFICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationText").value(hasItem(DEFAULT_NOTIFICATION_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(notificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(notificationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.notificationDate").value(DEFAULT_NOTIFICATION_DATE.toString()))
            .andExpect(jsonPath("$.notificationType").value(DEFAULT_NOTIFICATION_TYPE.toString()))
            .andExpect(jsonPath("$.notificationText").value(DEFAULT_NOTIFICATION_TEXT))
            .andExpect(jsonPath("$.isDelivered").value(DEFAULT_IS_DELIVERED.booleanValue()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationShouldBeFound("id.equals=" + id);
        defaultNotificationShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate equals to DEFAULT_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the notificationList where creationDate equals to UPDATED_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the notificationList where creationDate equals to UPDATED_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate is not null
        defaultNotificationShouldBeFound("creationDate.specified=true");

        // Get all the notificationList where creationDate is null
        defaultNotificationShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the notificationList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the notificationList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate is less than DEFAULT_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the notificationList where creationDate is less than UPDATED_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultNotificationShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the notificationList where creationDate is greater than SMALLER_CREATION_DATE
        defaultNotificationShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate equals to DEFAULT_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.equals=" + DEFAULT_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate equals to UPDATED_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.equals=" + UPDATED_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate in DEFAULT_NOTIFICATION_DATE or UPDATED_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.in=" + DEFAULT_NOTIFICATION_DATE + "," + UPDATED_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate equals to UPDATED_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.in=" + UPDATED_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate is not null
        defaultNotificationShouldBeFound("notificationDate.specified=true");

        // Get all the notificationList where notificationDate is null
        defaultNotificationShouldNotBeFound("notificationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate is greater than or equal to DEFAULT_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.greaterThanOrEqual=" + DEFAULT_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate is greater than or equal to UPDATED_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.greaterThanOrEqual=" + UPDATED_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate is less than or equal to DEFAULT_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.lessThanOrEqual=" + DEFAULT_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate is less than or equal to SMALLER_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.lessThanOrEqual=" + SMALLER_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate is less than DEFAULT_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.lessThan=" + DEFAULT_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate is less than UPDATED_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.lessThan=" + UPDATED_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationDate is greater than DEFAULT_NOTIFICATION_DATE
        defaultNotificationShouldNotBeFound("notificationDate.greaterThan=" + DEFAULT_NOTIFICATION_DATE);

        // Get all the notificationList where notificationDate is greater than SMALLER_NOTIFICATION_DATE
        defaultNotificationShouldBeFound("notificationDate.greaterThan=" + SMALLER_NOTIFICATION_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType equals to DEFAULT_NOTIFICATION_TYPE
        defaultNotificationShouldBeFound("notificationType.equals=" + DEFAULT_NOTIFICATION_TYPE);

        // Get all the notificationList where notificationType equals to UPDATED_NOTIFICATION_TYPE
        defaultNotificationShouldNotBeFound("notificationType.equals=" + UPDATED_NOTIFICATION_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType in DEFAULT_NOTIFICATION_TYPE or UPDATED_NOTIFICATION_TYPE
        defaultNotificationShouldBeFound("notificationType.in=" + DEFAULT_NOTIFICATION_TYPE + "," + UPDATED_NOTIFICATION_TYPE);

        // Get all the notificationList where notificationType equals to UPDATED_NOTIFICATION_TYPE
        defaultNotificationShouldNotBeFound("notificationType.in=" + UPDATED_NOTIFICATION_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationType is not null
        defaultNotificationShouldBeFound("notificationType.specified=true");

        // Get all the notificationList where notificationType is null
        defaultNotificationShouldNotBeFound("notificationType.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTextIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationText equals to DEFAULT_NOTIFICATION_TEXT
        defaultNotificationShouldBeFound("notificationText.equals=" + DEFAULT_NOTIFICATION_TEXT);

        // Get all the notificationList where notificationText equals to UPDATED_NOTIFICATION_TEXT
        defaultNotificationShouldNotBeFound("notificationText.equals=" + UPDATED_NOTIFICATION_TEXT);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTextIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationText in DEFAULT_NOTIFICATION_TEXT or UPDATED_NOTIFICATION_TEXT
        defaultNotificationShouldBeFound("notificationText.in=" + DEFAULT_NOTIFICATION_TEXT + "," + UPDATED_NOTIFICATION_TEXT);

        // Get all the notificationList where notificationText equals to UPDATED_NOTIFICATION_TEXT
        defaultNotificationShouldNotBeFound("notificationText.in=" + UPDATED_NOTIFICATION_TEXT);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationText is not null
        defaultNotificationShouldBeFound("notificationText.specified=true");

        // Get all the notificationList where notificationText is null
        defaultNotificationShouldNotBeFound("notificationText.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTextContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationText contains DEFAULT_NOTIFICATION_TEXT
        defaultNotificationShouldBeFound("notificationText.contains=" + DEFAULT_NOTIFICATION_TEXT);

        // Get all the notificationList where notificationText contains UPDATED_NOTIFICATION_TEXT
        defaultNotificationShouldNotBeFound("notificationText.contains=" + UPDATED_NOTIFICATION_TEXT);
    }

    @Test
    @Transactional
    void getAllNotificationsByNotificationTextNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where notificationText does not contain DEFAULT_NOTIFICATION_TEXT
        defaultNotificationShouldNotBeFound("notificationText.doesNotContain=" + DEFAULT_NOTIFICATION_TEXT);

        // Get all the notificationList where notificationText does not contain UPDATED_NOTIFICATION_TEXT
        defaultNotificationShouldBeFound("notificationText.doesNotContain=" + UPDATED_NOTIFICATION_TEXT);
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeliveredIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDelivered equals to DEFAULT_IS_DELIVERED
        defaultNotificationShouldBeFound("isDelivered.equals=" + DEFAULT_IS_DELIVERED);

        // Get all the notificationList where isDelivered equals to UPDATED_IS_DELIVERED
        defaultNotificationShouldNotBeFound("isDelivered.equals=" + UPDATED_IS_DELIVERED);
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeliveredIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDelivered in DEFAULT_IS_DELIVERED or UPDATED_IS_DELIVERED
        defaultNotificationShouldBeFound("isDelivered.in=" + DEFAULT_IS_DELIVERED + "," + UPDATED_IS_DELIVERED);

        // Get all the notificationList where isDelivered equals to UPDATED_IS_DELIVERED
        defaultNotificationShouldNotBeFound("isDelivered.in=" + UPDATED_IS_DELIVERED);
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeliveredIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDelivered is not null
        defaultNotificationShouldBeFound("isDelivered.specified=true");

        // Get all the notificationList where isDelivered is null
        defaultNotificationShouldNotBeFound("isDelivered.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDeleted equals to DEFAULT_IS_DELETED
        defaultNotificationShouldBeFound("isDeleted.equals=" + DEFAULT_IS_DELETED);

        // Get all the notificationList where isDeleted equals to UPDATED_IS_DELETED
        defaultNotificationShouldNotBeFound("isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDeleted in DEFAULT_IS_DELETED or UPDATED_IS_DELETED
        defaultNotificationShouldBeFound("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED);

        // Get all the notificationList where isDeleted equals to UPDATED_IS_DELETED
        defaultNotificationShouldNotBeFound("isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllNotificationsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where isDeleted is not null
        defaultNotificationShouldBeFound("isDeleted.specified=true");

        // Get all the notificationList where isDeleted is null
        defaultNotificationShouldNotBeFound("isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            notificationRepository.saveAndFlush(notification);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        notification.setUser(user);
        notificationRepository.saveAndFlush(notification);
        String userId = user.getId();

        // Get all the notificationList where user equals to userId
        defaultNotificationShouldBeFound("userId.equals=" + userId);

        // Get all the notificationList where user equals to "invalid-id"
        defaultNotificationShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationDate").value(hasItem(DEFAULT_NOTIFICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationText").value(hasItem(DEFAULT_NOTIFICATION_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notificationSearchRepository.save(notification);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .creationDate(UPDATED_CREATION_DATE)
            .notificationDate(UPDATED_NOTIFICATION_DATE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationText(UPDATED_NOTIFICATION_TEXT)
            .isDelivered(UPDATED_IS_DELIVERED)
            .isDeleted(UPDATED_IS_DELETED);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testNotification.getNotificationDate()).isEqualTo(UPDATED_NOTIFICATION_DATE);
        assertThat(testNotification.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationText()).isEqualTo(UPDATED_NOTIFICATION_TEXT);
        assertThat(testNotification.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
        assertThat(testNotification.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Notification> notificationSearchList = IterableUtils.toList(notificationSearchRepository.findAll());
                Notification testNotificationSearch = notificationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testNotificationSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
                assertThat(testNotificationSearch.getNotificationDate()).isEqualTo(UPDATED_NOTIFICATION_DATE);
                assertThat(testNotificationSearch.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
                assertThat(testNotificationSearch.getNotificationText()).isEqualTo(UPDATED_NOTIFICATION_TEXT);
                assertThat(testNotificationSearch.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
                assertThat(testNotificationSearch.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
            });
    }

    @Test
    @Transactional
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationText(UPDATED_NOTIFICATION_TEXT)
            .isDelivered(UPDATED_IS_DELIVERED)
            .isDeleted(UPDATED_IS_DELETED);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testNotification.getNotificationDate()).isEqualTo(DEFAULT_NOTIFICATION_DATE);
        assertThat(testNotification.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationText()).isEqualTo(UPDATED_NOTIFICATION_TEXT);
        assertThat(testNotification.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
        assertThat(testNotification.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .creationDate(UPDATED_CREATION_DATE)
            .notificationDate(UPDATED_NOTIFICATION_DATE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationText(UPDATED_NOTIFICATION_TEXT)
            .isDelivered(UPDATED_IS_DELIVERED)
            .isDeleted(UPDATED_IS_DELETED);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testNotification.getNotificationDate()).isEqualTo(UPDATED_NOTIFICATION_DATE);
        assertThat(testNotification.getNotificationType()).isEqualTo(UPDATED_NOTIFICATION_TYPE);
        assertThat(testNotification.getNotificationText()).isEqualTo(UPDATED_NOTIFICATION_TEXT);
        assertThat(testNotification.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
        assertThat(testNotification.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);
        notificationRepository.save(notification);
        notificationSearchRepository.save(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNotification() throws Exception {
        // Initialize the database
        notification = notificationRepository.saveAndFlush(notification);
        notificationSearchRepository.save(notification);

        // Search the notification
        restNotificationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationDate").value(hasItem(DEFAULT_NOTIFICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationText").value(hasItem(DEFAULT_NOTIFICATION_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())));
    }
}
