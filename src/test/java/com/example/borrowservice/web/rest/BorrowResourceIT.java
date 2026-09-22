package com.example.borrowservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.borrowservice.IntegrationTest;
import com.example.borrowservice.domain.Borrow;
import com.example.borrowservice.domain.enumeration.BorrowStatus;
import com.example.borrowservice.repository.BorrowRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BorrowResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BorrowResourceIT {

    private static final Long DEFAULT_BOOK_ID = 1L;
    private static final Long UPDATED_BOOK_ID = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Instant DEFAULT_BORROWED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BORROWED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RETURNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RETURNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BorrowStatus DEFAULT_STATUS = BorrowStatus.ACTIVE;
    private static final BorrowStatus UPDATED_STATUS = BorrowStatus.RETURNED;

    private static final String ENTITY_API_URL = "/api/borrows";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowMockMvc;

    private Borrow borrow;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrow createEntity(EntityManager em) {
        Borrow borrow = new Borrow()
            .bookId(DEFAULT_BOOK_ID)
            .userId(DEFAULT_USER_ID)
            .borrowedAt(DEFAULT_BORROWED_AT)
            .dueDate(DEFAULT_DUE_DATE)
            .returnedAt(DEFAULT_RETURNED_AT)
            .status(DEFAULT_STATUS);
        return borrow;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrow createUpdatedEntity(EntityManager em) {
        Borrow borrow = new Borrow()
            .bookId(UPDATED_BOOK_ID)
            .userId(UPDATED_USER_ID)
            .borrowedAt(UPDATED_BORROWED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .returnedAt(UPDATED_RETURNED_AT)
            .status(UPDATED_STATUS);
        return borrow;
    }

    @BeforeEach
    public void initTest() {
        borrow = createEntity(em);
    }

    @Test
    @Transactional
    void createBorrow() throws Exception {
        int databaseSizeBeforeCreate = borrowRepository.findAll().size();
        // Create the Borrow
        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isCreated());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeCreate + 1);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBookId()).isEqualTo(DEFAULT_BOOK_ID);
        assertThat(testBorrow.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testBorrow.getBorrowedAt()).isEqualTo(DEFAULT_BORROWED_AT);
        assertThat(testBorrow.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testBorrow.getReturnedAt()).isEqualTo(DEFAULT_RETURNED_AT);
        assertThat(testBorrow.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createBorrowWithExistingId() throws Exception {
        // Create the Borrow with an existing ID
        borrow.setId(1L);

        int databaseSizeBeforeCreate = borrowRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBookIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowRepository.findAll().size();
        // set the field null
        borrow.setBookId(null);

        // Create the Borrow, which fails.

        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowRepository.findAll().size();
        // set the field null
        borrow.setUserId(null);

        // Create the Borrow, which fails.

        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBorrowedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowRepository.findAll().size();
        // set the field null
        borrow.setBorrowedAt(null);

        // Create the Borrow, which fails.

        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowRepository.findAll().size();
        // set the field null
        borrow.setDueDate(null);

        // Create the Borrow, which fails.

        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowRepository.findAll().size();
        // set the field null
        borrow.setStatus(null);

        // Create the Borrow, which fails.

        restBorrowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBorrows() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        // Get all the borrowList
        restBorrowMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrow.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookId").value(hasItem(DEFAULT_BOOK_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].borrowedAt").value(hasItem(DEFAULT_BORROWED_AT.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnedAt").value(hasItem(DEFAULT_RETURNED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        // Get the borrow
        restBorrowMockMvc
            .perform(get(ENTITY_API_URL_ID, borrow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrow.getId().intValue()))
            .andExpect(jsonPath("$.bookId").value(DEFAULT_BOOK_ID.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.borrowedAt").value(DEFAULT_BORROWED_AT.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.returnedAt").value(DEFAULT_RETURNED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBorrow() throws Exception {
        // Get the borrow
        restBorrowMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();

        // Update the borrow
        Borrow updatedBorrow = borrowRepository.findById(borrow.getId()).get();
        // Disconnect from session so that the updates on updatedBorrow are not directly saved in db
        em.detach(updatedBorrow);
        updatedBorrow
            .bookId(UPDATED_BOOK_ID)
            .userId(UPDATED_USER_ID)
            .borrowedAt(UPDATED_BORROWED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .returnedAt(UPDATED_RETURNED_AT)
            .status(UPDATED_STATUS);

        restBorrowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBorrow.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBorrow))
            )
            .andExpect(status().isOk());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBookId()).isEqualTo(UPDATED_BOOK_ID);
        assertThat(testBorrow.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testBorrow.getBorrowedAt()).isEqualTo(UPDATED_BORROWED_AT);
        assertThat(testBorrow.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBorrow.getReturnedAt()).isEqualTo(UPDATED_RETURNED_AT);
        assertThat(testBorrow.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrow.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrow))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrow))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBorrowWithPatch() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();

        // Update the borrow using partial update
        Borrow partialUpdatedBorrow = new Borrow();
        partialUpdatedBorrow.setId(borrow.getId());

        partialUpdatedBorrow.bookId(UPDATED_BOOK_ID).userId(UPDATED_USER_ID).borrowedAt(UPDATED_BORROWED_AT).status(UPDATED_STATUS);

        restBorrowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrow.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrow))
            )
            .andExpect(status().isOk());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBookId()).isEqualTo(UPDATED_BOOK_ID);
        assertThat(testBorrow.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testBorrow.getBorrowedAt()).isEqualTo(UPDATED_BORROWED_AT);
        assertThat(testBorrow.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testBorrow.getReturnedAt()).isEqualTo(DEFAULT_RETURNED_AT);
        assertThat(testBorrow.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateBorrowWithPatch() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();

        // Update the borrow using partial update
        Borrow partialUpdatedBorrow = new Borrow();
        partialUpdatedBorrow.setId(borrow.getId());

        partialUpdatedBorrow
            .bookId(UPDATED_BOOK_ID)
            .userId(UPDATED_USER_ID)
            .borrowedAt(UPDATED_BORROWED_AT)
            .dueDate(UPDATED_DUE_DATE)
            .returnedAt(UPDATED_RETURNED_AT)
            .status(UPDATED_STATUS);

        restBorrowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrow.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrow))
            )
            .andExpect(status().isOk());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBookId()).isEqualTo(UPDATED_BOOK_ID);
        assertThat(testBorrow.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testBorrow.getBorrowedAt()).isEqualTo(UPDATED_BORROWED_AT);
        assertThat(testBorrow.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBorrow.getReturnedAt()).isEqualTo(UPDATED_RETURNED_AT);
        assertThat(testBorrow.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, borrow.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrow))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrow))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();
        borrow.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        int databaseSizeBeforeDelete = borrowRepository.findAll().size();

        // Delete the borrow
        restBorrowMockMvc
            .perform(delete(ENTITY_API_URL_ID, borrow.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
