package com.finance.backend.service;

import com.finance.backend.dto.CategorySummaryDto;
import com.finance.backend.dto.DashboardSummaryDto;
import com.finance.backend.dto.InsightDto;
import com.finance.backend.dto.MonthlyTrendDto;
import com.finance.backend.dto.TransactionRequestDto;
import com.finance.backend.dto.TransactionResponseDto;
import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import com.finance.backend.repository.AppUserRepository;
import com.finance.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AppUserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, AppUserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponseDto saveTransaction(TransactionRequestDto requestDto, String email) {
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDto.getAmount());
        transaction.setType(requestDto.getType());
        transaction.setCategory(formatCategory(requestDto.getCategory()));
        transaction.setDescription(requestDto.getDescription());
        transaction.setTransactionDate(requestDto.getTransactionDate());
        transaction.setSource(requestDto.getSource());
        transaction.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")));

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponseDto(savedTransaction);
    }

    public List<TransactionResponseDto> getAllTransactions(String email) {
        return findTransactions(email)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public void deleteTransaction(Long id, String email) {
        Transaction transaction = transactionRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        transactionRepository.delete(transaction);
    }

    public DashboardSummaryDto getDashboardSummary(String email) {
        List<Transaction> transactions = findTransactions(email);
        BigDecimal totalIncome = transactions
                .stream()
                .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions
                .stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return new DashboardSummaryDto(totalIncome, totalExpense, netBalance);
    }

    public List<CategorySummaryDto> getExpenseByCategory(String email) {
        return findTransactions(email)
                .stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> new CategorySummaryDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<MonthlyTrendDto> getMonthlyTrend(String email) {
        Map<String, BigDecimal> incomeMap = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseMap = new LinkedHashMap<>();

        for (Transaction transaction : findTransactions(email)) {
            String month = transaction.getTransactionDate().getYear() + "-" +
                    String.format("%02d", transaction.getTransactionDate().getMonthValue());

            if (transaction.getType() == TransactionType.INCOME) {
                incomeMap.put(
                        month,
                        incomeMap.getOrDefault(month, BigDecimal.ZERO).add(transaction.getAmount())
                );
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                expenseMap.put(
                        month,
                        expenseMap.getOrDefault(month, BigDecimal.ZERO).add(transaction.getAmount())
                );
            }
        }

        Set<String> months = new TreeSet<>();
        months.addAll(incomeMap.keySet());
        months.addAll(expenseMap.keySet());

        return months.stream()
                .map(month -> new MonthlyTrendDto(
                        month,
                        incomeMap.getOrDefault(month, BigDecimal.ZERO),
                        expenseMap.getOrDefault(month, BigDecimal.ZERO)
                ))
                .toList();
    }

    public List<InsightDto> getInsights(String email) {
        List<Transaction> transactions = findTransactions(email);
        List<InsightDto> insights = new ArrayList<>();

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(totalIncome) > 0) {
            insights.add(new InsightDto("Your total expenses are higher than your total income."));
        } else {
            insights.add(new InsightDto("Your total income is currently higher than your total expenses."));
        }

        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> insights.add(
                        new InsightDto(
                                "Your highest spending category is " + entry.getKey()
                                        + " with total spending of " + entry.getValue()
                        )
                ));

        if (categoryTotals.containsKey("Food")) {
            insights.add(new InsightDto("Food is one of your active spending categories. You may want to monitor dining and grocery habits."));
        }

        if (categoryTotals.containsKey("Transport")) {
            insights.add(new InsightDto("Transport appears as a recurring expense category."));
        }

        return insights;
    }

    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getSource()
        );
    }

    private List<Transaction> findTransactions(String email) {
        return transactionRepository.findAllByUserEmailOrderByTransactionDateDescIdDesc(email);
    }

    private String formatCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return category;
        }

        String cleaned = category.trim().toLowerCase();
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
    }
}
