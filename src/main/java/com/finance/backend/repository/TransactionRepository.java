package com.finance.backend.repository;

import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(TransactionType type);
}