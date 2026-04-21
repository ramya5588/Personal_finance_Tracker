package com.finance.backend.service;

import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import com.finance.backend.repository.TransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
public class CsvService {

    private final TransactionRepository transactionRepository;

    public CsvService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void uploadCsv(MultipartFile file) {
        try (
                BufferedReader fileReader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
                );
                CSVParser csvParser = new CSVParser(
                        fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()
                )
        ) {
            for (CSVRecord csvRecord : csvParser) {
                Transaction transaction = new Transaction();

                transaction.setAmount(new BigDecimal(csvRecord.get("amount")));
                transaction.setType(TransactionType.valueOf(csvRecord.get("type").toUpperCase()));
                transaction.setCategory(csvRecord.get("category"));
                transaction.setDescription(csvRecord.get("description"));
                transaction.setTransactionDate(LocalDate.parse(csvRecord.get("transactionDate")));
                transaction.setSource(csvRecord.get("source"));

                transactionRepository.save(transaction);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse and save CSV file: " + e.getMessage());
        }
    }
}