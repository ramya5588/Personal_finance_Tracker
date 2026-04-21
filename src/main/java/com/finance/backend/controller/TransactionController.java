package com.finance.backend.controller;

import com.finance.backend.dto.TransactionRequestDto;
import com.finance.backend.dto.TransactionResponseDto;
import com.finance.backend.service.CsvService;
import com.finance.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final CsvService csvService;

    public TransactionController(TransactionService transactionService, CsvService csvService) {
        this.transactionService = transactionService;
        this.csvService = csvService;
    }

    @PostMapping
    public TransactionResponseDto createTransaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        return transactionService.saveTransaction(requestDto);
    }

    @GetMapping
    public List<TransactionResponseDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping("/upload")
    public String uploadTransactionsCsv(@RequestParam("file") MultipartFile file) {
        csvService.uploadCsv(file);
        return "CSV uploaded successfully";
    }
}