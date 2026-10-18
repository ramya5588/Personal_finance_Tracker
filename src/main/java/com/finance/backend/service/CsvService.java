package com.finance.backend.service;

import com.finance.backend.entity.Transaction;
import com.finance.backend.entity.TransactionType;
import com.finance.backend.repository.TransactionRepository;
import com.finance.backend.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
public class CsvService {

    private final TransactionRepository transactionRepository;
    private final AppUserRepository userRepository;

    public CsvService(TransactionRepository transactionRepository, AppUserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public void uploadCsv(MultipartFile file, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        try (
                BufferedReader fileReader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
                );
                CSVParser csvParser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build()
                        .parse(fileReader)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                Transaction transaction = new Transaction();

                transaction.setAmount(new BigDecimal(csvRecord.get("amount")));
                transaction.setType(TransactionType.valueOf(csvRecord.get("type").toUpperCase()));
                transaction.setCategory(csvRecord.get("category"));
                transaction.setDescription(csvRecord.get("description"));
                transaction.setTransactionDate(LocalDate.parse(csvRecord.get("transactionDate")));
                transaction.setSource(csvRecord.get("source"));
                transaction.setUser(user);

                transactionRepository.save(transaction);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse and save CSV file: " + e.getMessage());
        }
    }
}
