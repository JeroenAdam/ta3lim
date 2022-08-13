package com.ta3lim.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ta3lim.app.IntegrationTest;
import com.ta3lim.app.domain.Message;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.repository.MessageRepository;
import com.ta3lim.app.repository.search.MessageSearchRepository;
import com.ta3lim.app.service.MessageService;
import com.ta3lim.app.service.criteria.MessageCriteria;
import com.ta3lim.app.service.dto.MessageDTO;
import com.ta3lim.app.service.mapper.MessageMapper;
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
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MessageResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_MESSAGE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_TEXT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELIVERED = false;
    private static final Boolean UPDATED_IS_DELIVERED = true;

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/messages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MessageRepository messageRepository;

    @Mock
    private MessageRepository messageRepositoryMock;

    @Autowired
    private MessageMapper messageMapper;

    @Mock
    private MessageService messageServiceMock;

    @Autowired
    private MessageSearchRepository messageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageMockMvc;

    private Message message;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .creationDate(DEFAULT_CREATION_DATE)
            .messageText(DEFAULT_MESSAGE_TEXT)
            .isDelivered(DEFAULT_IS_DELIVERED);
        return message;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager em) {
        Message message = new Message()
            .creationDate(UPDATED_CREATION_DATE)
            .messageText(UPDATED_MESSAGE_TEXT)
            .isDelivered(UPDATED_IS_DELIVERED);
        return message;
    }

    @BeforeEach
    public void initTest() {
        messageSearchRepository.deleteAll();
        message = createEntity(em);
    }

    @Test
    @Transactional
    void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        restMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testMessage.getMessageText()).isEqualTo(DEFAULT_MESSAGE_TEXT);
        assertThat(testMessage.getIsDelivered()).isEqualTo(DEFAULT_IS_DELIVERED);
    }

    @Test
    @Transactional
    void createMessageWithExistingId() throws Exception {
        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        int databaseSizeBeforeCreate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(messageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(messageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.messageText").value(DEFAULT_MESSAGE_TEXT))
            .andExpect(jsonPath("$.isDelivered").value(DEFAULT_IS_DELIVERED.booleanValue()));
    }

    @Test
    @Transactional
    void getMessagesByIdFiltering() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        Long id = message.getId();

        defaultMessageShouldBeFound("id.equals=" + id);
        defaultMessageShouldNotBeFound("id.notEquals=" + id);

        defaultMessageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.greaterThan=" + id);

        defaultMessageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate equals to DEFAULT_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the messageList where creationDate equals to UPDATED_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the messageList where creationDate equals to UPDATED_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate is not null
        defaultMessageShouldBeFound("creationDate.specified=true");

        // Get all the messageList where creationDate is null
        defaultMessageShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate is greater than or equal to DEFAULT_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the messageList where creationDate is greater than or equal to UPDATED_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate is less than or equal to DEFAULT_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE);

        // Get all the messageList where creationDate is less than or equal to SMALLER_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate is less than DEFAULT_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the messageList where creationDate is less than UPDATED_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.lessThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByCreationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where creationDate is greater than DEFAULT_CREATION_DATE
        defaultMessageShouldNotBeFound("creationDate.greaterThan=" + DEFAULT_CREATION_DATE);

        // Get all the messageList where creationDate is greater than SMALLER_CREATION_DATE
        defaultMessageShouldBeFound("creationDate.greaterThan=" + SMALLER_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageTextIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where messageText equals to DEFAULT_MESSAGE_TEXT
        defaultMessageShouldBeFound("messageText.equals=" + DEFAULT_MESSAGE_TEXT);

        // Get all the messageList where messageText equals to UPDATED_MESSAGE_TEXT
        defaultMessageShouldNotBeFound("messageText.equals=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageTextIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where messageText in DEFAULT_MESSAGE_TEXT or UPDATED_MESSAGE_TEXT
        defaultMessageShouldBeFound("messageText.in=" + DEFAULT_MESSAGE_TEXT + "," + UPDATED_MESSAGE_TEXT);

        // Get all the messageList where messageText equals to UPDATED_MESSAGE_TEXT
        defaultMessageShouldNotBeFound("messageText.in=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where messageText is not null
        defaultMessageShouldBeFound("messageText.specified=true");

        // Get all the messageList where messageText is null
        defaultMessageShouldNotBeFound("messageText.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByMessageTextContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where messageText contains DEFAULT_MESSAGE_TEXT
        defaultMessageShouldBeFound("messageText.contains=" + DEFAULT_MESSAGE_TEXT);

        // Get all the messageList where messageText contains UPDATED_MESSAGE_TEXT
        defaultMessageShouldNotBeFound("messageText.contains=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageTextNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where messageText does not contain DEFAULT_MESSAGE_TEXT
        defaultMessageShouldNotBeFound("messageText.doesNotContain=" + DEFAULT_MESSAGE_TEXT);

        // Get all the messageList where messageText does not contain UPDATED_MESSAGE_TEXT
        defaultMessageShouldBeFound("messageText.doesNotContain=" + UPDATED_MESSAGE_TEXT);
    }

    @Test
    @Transactional
    void getAllMessagesByIsDeliveredIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where isDelivered equals to DEFAULT_IS_DELIVERED
        defaultMessageShouldBeFound("isDelivered.equals=" + DEFAULT_IS_DELIVERED);

        // Get all the messageList where isDelivered equals to UPDATED_IS_DELIVERED
        defaultMessageShouldNotBeFound("isDelivered.equals=" + UPDATED_IS_DELIVERED);
    }

    @Test
    @Transactional
    void getAllMessagesByIsDeliveredIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where isDelivered in DEFAULT_IS_DELIVERED or UPDATED_IS_DELIVERED
        defaultMessageShouldBeFound("isDelivered.in=" + DEFAULT_IS_DELIVERED + "," + UPDATED_IS_DELIVERED);

        // Get all the messageList where isDelivered equals to UPDATED_IS_DELIVERED
        defaultMessageShouldNotBeFound("isDelivered.in=" + UPDATED_IS_DELIVERED);
    }

    @Test
    @Transactional
    void getAllMessagesByIsDeliveredIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where isDelivered is not null
        defaultMessageShouldBeFound("isDelivered.specified=true");

        // Get all the messageList where isDelivered is null
        defaultMessageShouldNotBeFound("isDelivered.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByReceiverIsEqualToSomething() throws Exception {
        User receiver;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            receiver = UserResourceIT.createEntity(em);
        } else {
            receiver = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(receiver);
        em.flush();
        message.setReceiver(receiver);
        messageRepository.saveAndFlush(message);
        String receiverId = receiver.getId();

        // Get all the messageList where receiver equals to receiverId
        defaultMessageShouldBeFound("receiverId.equals=" + receiverId);

        // Get all the messageList where receiver equals to "invalid-id"
        defaultMessageShouldNotBeFound("receiverId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllMessagesBySenderIsEqualToSomething() throws Exception {
        User sender;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            sender = UserResourceIT.createEntity(em);
        } else {
            sender = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(sender);
        em.flush();
        message.setSender(sender);
        messageRepository.saveAndFlush(message);
        String senderId = sender.getId();

        // Get all the messageList where sender equals to senderId
        defaultMessageShouldBeFound("senderId.equals=" + senderId);

        // Get all the messageList where sender equals to "invalid-id"
        defaultMessageShouldNotBeFound("senderId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageShouldBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())));

        // Check, that the count call also returns 1
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageShouldNotBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        messageSearchRepository.save(message);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).get();
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage.creationDate(UPDATED_CREATION_DATE).messageText(UPDATED_MESSAGE_TEXT).isDelivered(UPDATED_IS_DELIVERED);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testMessage.getMessageText()).isEqualTo(UPDATED_MESSAGE_TEXT);
        assertThat(testMessage.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Message> messageSearchList = IterableUtils.toList(messageSearchRepository.findAll());
                Message testMessageSearch = messageSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testMessageSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
                assertThat(testMessageSearch.getMessageText()).isEqualTo(UPDATED_MESSAGE_TEXT);
                assertThat(testMessageSearch.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
            });
    }

    @Test
    @Transactional
    void putNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testMessage.getMessageText()).isEqualTo(DEFAULT_MESSAGE_TEXT);
        assertThat(testMessage.getIsDelivered()).isEqualTo(DEFAULT_IS_DELIVERED);
    }

    @Test
    @Transactional
    void fullUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.creationDate(UPDATED_CREATION_DATE).messageText(UPDATED_MESSAGE_TEXT).isDelivered(UPDATED_IS_DELIVERED);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testMessage.getMessageText()).isEqualTo(UPDATED_MESSAGE_TEXT);
        assertThat(testMessage.getIsDelivered()).isEqualTo(UPDATED_IS_DELIVERED);
    }

    @Test
    @Transactional
    void patchNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, messageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        message.setId(count.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);
        messageRepository.save(message);
        messageSearchRepository.save(message);

        int databaseSizeBeforeDelete = messageRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the message
        restMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, message.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(messageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMessage() throws Exception {
        // Initialize the database
        message = messageRepository.saveAndFlush(message);
        messageSearchRepository.save(message);

        // Search the message
        restMessageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].isDelivered").value(hasItem(DEFAULT_IS_DELIVERED.booleanValue())));
    }
}
