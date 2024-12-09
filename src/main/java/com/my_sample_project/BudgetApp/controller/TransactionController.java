package com.my_sample_project.BudgetApp.controller;

import com.my_sample_project.BudgetApp.dto.transaction.TransactionDTO;
import com.my_sample_project.BudgetApp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TransactionDTO transactionDTO) {

        // Save the transaction using the service method
        TransactionDTO savedTransaction = transactionService.saveTransaction(authHeader, transactionDTO);

        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @RequestHeader("Authorization") String authHeader) {

        // Fetch all transactions using the service method
        List<TransactionDTO> transactions = transactionService.getAllTransactions(authHeader);

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}