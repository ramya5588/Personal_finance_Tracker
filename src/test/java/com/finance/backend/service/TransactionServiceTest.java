package com.finance.backend.service;

import com.finance.backend.dto.MonthlyTrendDto;
import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import com.finance.backend.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void monthlyTrendIncludesIncomeOnlyAndExpenseOnlyMonthsInOrder() {
        when(transactionRepository.findAll()).thenReturn(List.of(
                transaction(TransactionType.EXPENSE, "25.00", LocalDate.of(2026, 2, 5)),
                transaction(TransactionType.INCOME, "100.00", LocalDate.of(2026, 1, 31))
        ));

        List<MonthlyTrendDto> result = transactionService.getMonthlyTrend();

        assertEquals(List.of("2026-01", "2026-02"), result.stream().map(MonthlyTrendDto::getMonth).toList());
        assertEquals(new BigDecimal("100.00"), result.get(0).getIncome());
        assertEquals(BigDecimal.ZERO, result.get(0).getExpense());
        assertEquals(BigDecimal.ZERO, result.get(1).getIncome());
        assertEquals(new BigDecimal("25.00"), result.get(1).getExpense());
    }

    @Test
    void deleteTransactionDeletesExistingTransaction() {
        when(transactionRepository.existsById(7L)).thenReturn(true);

        transactionService.deleteTransaction(7L);

        verify(transactionRepository).deleteById(7L);
    }

    @Test
    void deleteTransactionReturnsNotFoundForMissingTransaction() {
        when(transactionRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> transactionService.deleteTransaction(99L));
        verify(transactionRepository, never()).deleteById(99L);
    }

    private Transaction transaction(TransactionType type, String amount, LocalDate date) {
        return new Transaction(null, new BigDecimal(amount), type, "Test", "Test transaction", date, "TEST");
    }
}
