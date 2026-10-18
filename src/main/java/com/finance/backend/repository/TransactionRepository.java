package com.finance.backend.repository;

import com.finance.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserEmailOrderByTransactionDateDescIdDesc(String email);

    Optional<Transaction> findByIdAndUserEmail(Long id, String email);
}
