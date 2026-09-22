package com.example.borrowservice.repository;

import com.example.borrowservice.domain.Borrow;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Borrow entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {}
