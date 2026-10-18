package com.finance.backend.controller;

import com.finance.backend.dto.TransactionRequestDto;
import com.finance.backend.dto.TransactionResponseDto;
import com.finance.backend.service.CsvService;
import com.finance.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CsvService csvService;

    public TransactionController(TransactionService transactionService, CsvService csvService) {
        this.transactionService = transactionService;
        this.csvService = csvService;
    }

    @PostMapping
    public TransactionResponseDto createTransaction(@Valid @RequestBody TransactionRequestDto requestDto, Authentication authentication) {
        return transactionService.saveTransaction(requestDto, authentication.getName());
    }

    @GetMapping
    public List<TransactionResponseDto> getAllTransactions(Authentication authentication) {
        return transactionService.getAllTransactions(authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id, Authentication authentication) {
        transactionService.deleteTransaction(id, authentication.getName());
    }

    @PostMapping("/upload")
    public String uploadTransactionsCsv(@RequestParam("file") MultipartFile file, Authentication authentication) {
        csvService.uploadCsv(file, authentication.getName());
        return "CSV uploaded successfully";
    }
}
