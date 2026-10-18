package com.finance.backend.repository;

import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@Transactional
class TransactionRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayCreatesAndTracksTheSchema() {
        Integer appliedMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class
        );

        assertTrue(appliedMigrations != null && appliedMigrations >= 2);
    }

    @Test
    void savesAndReadsATransactionUsingPostgreSql() {
        Transaction transaction = new Transaction(
                null,
                new BigDecimal("42.75"),
                TransactionType.EXPENSE,
                "Groceries",
                "Integration test transaction",
                LocalDate.of(2026, 10, 18),
                "TEST"
        );

        Transaction saved = transactionRepository.saveAndFlush(transaction);
        Transaction reloaded = transactionRepository.findById(saved.getId()).orElseThrow();

        assertEquals(new BigDecimal("42.75"), reloaded.getAmount());
        assertEquals(TransactionType.EXPENSE, reloaded.getType());
        assertEquals("Groceries", reloaded.getCategory());
    }
}
