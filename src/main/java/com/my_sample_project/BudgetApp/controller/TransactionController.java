package com.my_sample_project.BudgetApp.controller;

import com.my_sample_project.BudgetApp.dto.transaction.TransactionDTO;
import com.my_sample_project.BudgetApp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions/income")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestHeader("Authorization") String authHeader,  // Extract the Authorization header
            @RequestBody TransactionDTO transactionDTO) {

        // Save the transaction and return the result
        TransactionDTO savedTransaction = transactionService.saveTransaction(authHeader, transactionDTO);

        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }
}
