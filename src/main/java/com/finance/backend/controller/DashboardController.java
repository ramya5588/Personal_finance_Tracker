package com.finance.backend.controller;

import com.finance.backend.dto.CategorySummaryDto;
import com.finance.backend.dto.DashboardSummaryDto;
import com.finance.backend.dto.InsightDto;
import com.finance.backend.dto.MonthlyTrendDto;
import com.finance.backend.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final TransactionService transactionService;

    public DashboardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDto getDashboardSummary(Authentication authentication) {
        return transactionService.getDashboardSummary(authentication.getName());
    }

    @GetMapping("/category-breakdown")
    public List<CategorySummaryDto> getExpenseByCategory(Authentication authentication) {
        return transactionService.getExpenseByCategory(authentication.getName());
    }

    @GetMapping("/monthly-trend")
    public List<MonthlyTrendDto> getMonthlyTrend(Authentication authentication) {
        return transactionService.getMonthlyTrend(authentication.getName());
    }

    @GetMapping("/insights")
    public List<InsightDto> getInsights(Authentication authentication) {
        return transactionService.getInsights(authentication.getName());
    }
}
