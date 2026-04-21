package com.finance.backend.service;

import com.finance.backend.dto.CategorySummaryDto;
import com.finance.backend.dto.DashboardSummaryDto;
import com.finance.backend.dto.InsightDto;
import com.finance.backend.dto.MonthlyTrendDto;
import com.finance.backend.dto.TransactionRequestDto;
import com.finance.backend.dto.TransactionResponseDto;
import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import com.finance.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponseDto saveTransaction(TransactionRequestDto requestDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDto.getAmount());
        transaction.setType(requestDto.getType());
        transaction.setCategory(formatCategory(requestDto.getCategory()));
        transaction.setDescription(requestDto.getDescription());
        transaction.setTransactionDate(requestDto.getTransactionDate());
        transaction.setSource(requestDto.getSource());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponseDto(savedTransaction);
    }

    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public DashboardSummaryDto getDashboardSummary() {
        BigDecimal totalIncome = transactionRepository.findByType(TransactionType.INCOME)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionRepository.findByType(TransactionType.EXPENSE)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return new DashboardSummaryDto(totalIncome, totalExpense, netBalance);
    }

    public List<CategorySummaryDto> getExpenseByCategory() {
        return transactionRepository.findAll()
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

    public List<MonthlyTrendDto> getMonthlyTrend() {
        Map<String, BigDecimal> incomeMap = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseMap = new LinkedHashMap<>();

        for (Transaction transaction : transactionRepository.findAll()) {
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

        return incomeMap.keySet().stream()
                .map(month -> new MonthlyTrendDto(
                        month,
                        incomeMap.getOrDefault(month, BigDecimal.ZERO),
                        expenseMap.getOrDefault(month, BigDecimal.ZERO)
                ))
                .toList();
    }

    public List<InsightDto> getInsights() {
        List<Transaction> transactions = transactionRepository.findAll();
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

    private String formatCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return category;
        }

        String cleaned = category.trim().toLowerCase();
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
    }
}