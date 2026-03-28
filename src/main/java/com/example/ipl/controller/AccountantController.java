package com.example.ipl.controller;

import com.example.ipl.model.Transaction;
import com.example.ipl.repositories.TransactionRepository;
import com.example.ipl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accountant")
public class AccountantController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String transactionId = body.get("transactionId");

        if (transactionId == null || transactionId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Transaction ID is required"));
        }

        if (transactionRepository.findByTransactionId(transactionId).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Transaction ID already exists"));
        }

        String accountantUsername = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setCreatedBy(accountantUsername);
        transaction.setUsed(false);

        transactionRepository.save(transaction);
        return ResponseEntity.ok(Map.of("message", "Transaction ID added successfully"));
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
