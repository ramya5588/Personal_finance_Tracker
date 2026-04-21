package com.finance.backend.controller;

import com.finance.backend.dto.CategorySummaryDto;
import com.finance.backend.dto.DashboardSummaryDto;
import com.finance.backend.dto.MonthlyTrendDto;
import com.finance.backend.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TransactionService transactionService;

    public DashboardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary() {
        return transactionService.getDashboardSummary();
    }

    @GetMapping("/category-breakdown")
    public List<CategorySummaryDto> getExpenseByCategory() {
        return transactionService.getExpenseByCategory();
    }

    @GetMapping("/monthly-trend")
    public List<MonthlyTrendDto> getMonthlyTrend() {
        return transactionService.getMonthlyTrend();
    }
}