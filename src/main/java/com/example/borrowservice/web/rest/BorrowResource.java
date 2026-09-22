package com.example.borrowservice.web.rest;

import com.example.borrowservice.domain.Borrow;
import com.example.borrowservice.repository.BorrowRepository;
import com.example.borrowservice.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.borrowservice.domain.Borrow}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BorrowResource {

    private final Logger log = LoggerFactory.getLogger(BorrowResource.class);

    private static final String ENTITY_NAME = "borrowServiceBorrow";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowRepository borrowRepository;

    public BorrowResource(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    /**
     * {@code POST  /borrows} : Create a new borrow.
     *
     * @param borrow the borrow to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new borrow, or with status {@code 400 (Bad Request)} if the borrow has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/borrows")
    public ResponseEntity<Borrow> createBorrow(@Valid @RequestBody Borrow borrow) throws URISyntaxException {
        log.debug("REST request to save Borrow : {}", borrow);
        if (borrow.getId() != null) {
            throw new BadRequestAlertException("A new borrow cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Borrow result = borrowRepository.save(borrow);
        return ResponseEntity
            .created(new URI("/api/borrows/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /borrows/:id} : Updates an existing borrow.
     *
     * @param id the id of the borrow to save.
     * @param borrow the borrow to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrow,
     * or with status {@code 400 (Bad Request)} if the borrow is not valid,
     * or with status {@code 500 (Internal Server Error)} if the borrow couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/borrows/{id}")
    public ResponseEntity<Borrow> updateBorrow(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Borrow borrow
    ) throws URISyntaxException {
        log.debug("REST request to update Borrow : {}, {}", id, borrow);
        if (borrow.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrow.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Borrow result = borrowRepository.save(borrow);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, borrow.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /borrows/:id} : Partial updates given fields of an existing borrow, field will ignore if it is null
     *
     * @param id the id of the borrow to save.
     * @param borrow the borrow to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrow,
     * or with status {@code 400 (Bad Request)} if the borrow is not valid,
     * or with status {@code 404 (Not Found)} if the borrow is not found,
     * or with status {@code 500 (Internal Server Error)} if the borrow couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/borrows/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Borrow> partialUpdateBorrow(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Borrow borrow
    ) throws URISyntaxException {
        log.debug("REST request to partial update Borrow partially : {}, {}", id, borrow);
        if (borrow.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrow.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Borrow> result = borrowRepository
            .findById(borrow.getId())
            .map(existingBorrow -> {
                if (borrow.getBookId() != null) {
                    existingBorrow.setBookId(borrow.getBookId());
                }
                if (borrow.getUserId() != null) {
                    existingBorrow.setUserId(borrow.getUserId());
                }
                if (borrow.getBorrowedAt() != null) {
                    existingBorrow.setBorrowedAt(borrow.getBorrowedAt());
                }
                if (borrow.getDueDate() != null) {
                    existingBorrow.setDueDate(borrow.getDueDate());
                }
                if (borrow.getReturnedAt() != null) {
                    existingBorrow.setReturnedAt(borrow.getReturnedAt());
                }
                if (borrow.getStatus() != null) {
                    existingBorrow.setStatus(borrow.getStatus());
                }

                return existingBorrow;
            })
            .map(borrowRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, borrow.getId().toString())
        );
    }

    /**
     * {@code GET  /borrows} : get all the borrows.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of borrows in body.
     */
    @GetMapping("/borrows")
    public List<Borrow> getAllBorrows() {
        log.debug("REST request to get all Borrows");
        return borrowRepository.findAll();
    }

    /**
     * {@code GET  /borrows/:id} : get the "id" borrow.
     *
     * @param id the id of the borrow to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the borrow, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/borrows/{id}")
    public ResponseEntity<Borrow> getBorrow(@PathVariable Long id) {
        log.debug("REST request to get Borrow : {}", id);
        Optional<Borrow> borrow = borrowRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(borrow);
    }

    /**
     * {@code DELETE  /borrows/:id} : delete the "id" borrow.
     *
     * @param id the id of the borrow to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/borrows/{id}")
    public ResponseEntity<Void> deleteBorrow(@PathVariable Long id) {
        log.debug("REST request to delete Borrow : {}", id);
        borrowRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
