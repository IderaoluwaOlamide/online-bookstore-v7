package com.example.borrowservice.domain;

import com.example.borrowservice.domain.enumeration.BorrowStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Borrow.
 */
@Entity
@Table(name = "borrow")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Borrow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "borrowed_at", nullable = false)
    private Instant borrowedAt;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Borrow id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public Borrow bookId(Long bookId) {
        this.setBookId(bookId);
        return this;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Borrow userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getBorrowedAt() {
        return this.borrowedAt;
    }

    public Borrow borrowedAt(Instant borrowedAt) {
        this.setBorrowedAt(borrowedAt);
        return this;
    }

    public void setBorrowedAt(Instant borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Borrow dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getReturnedAt() {
        return this.returnedAt;
    }

    public Borrow returnedAt(Instant returnedAt) {
        this.setReturnedAt(returnedAt);
        return this;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public BorrowStatus getStatus() {
        return this.status;
    }

    public Borrow status(BorrowStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Borrow)) {
            return false;
        }
        return id != null && id.equals(((Borrow) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Borrow{" +
            "id=" + getId() +
            ", bookId=" + getBookId() +
            ", userId=" + getUserId() +
            ", borrowedAt='" + getBorrowedAt() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", returnedAt='" + getReturnedAt() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
