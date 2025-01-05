package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.dto.account.AccountAmountUpdateDTO;
import com.my_sample_project.BudgetApp.dto.category.CategoryDTO;
import com.my_sample_project.BudgetApp.dto.transaction.TransactionDTO;
import com.my_sample_project.BudgetApp.exception.CategoryValidationException;
import com.my_sample_project.BudgetApp.exception.InsufficientFundsException;
import com.my_sample_project.BudgetApp.model.*;
import com.my_sample_project.BudgetApp.repository.CategoryRepository;
import com.my_sample_project.BudgetApp.repository.TransactionRepository;
import com.my_sample_project.BudgetApp.repository.UserRepository;
import com.my_sample_project.BudgetApp.util.JwtUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            JwtUtil jwtUtil,
            UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // Save a transaction (income, expense, or transfer)
    @Transactional(noRollbackFor = {InsufficientFundsException.class})
    public TransactionDTO saveTransaction(String authHeader, TransactionDTO transactionDTO) {
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Validate transaction and account details
        validateTransaction(userId, transactionDTO);

        // Fetch related entities
        User user = fetchUserById(userId);
        Category category = fetchCategoryById(transactionDTO.getCategory_id());

        // Initialize and save the transaction
        Transaction transaction = initializeTransaction(transactionDTO, user, category);
        transaction = transactionRepository.save(transaction);

        try {
            // Handle account balance updates based on transaction type
            handleAccountBalanceUpdates(transactionDTO, userId, category);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);
        } catch (InsufficientFundsException e) {
            handleTransactionFailure(transaction, e.getMessage());
            throw e;
        } catch (Exception e) {
            handleTransactionFailure(transaction, e.getMessage());
            throw new RuntimeException("An error occurred while saving the transaction", e);
        }

        // Return transaction as DTO
        return convertToTransactionDTO(transaction);
    }

    private void validateTransaction(Long userId, TransactionDTO transactionDTO) {
        System.out.println("Validating accounts...");

        if (transactionDTO.getLeftCategory() == null || transactionDTO.getLeftCategory().isEmpty()) {
            throw new CategoryValidationException("Left Category cannot be null or empty.", 400);
        }

        if (transactionDTO.getRightCategory() == null || transactionDTO.getRightCategory().isEmpty()) {
            throw new CategoryValidationException("Right Category cannot be null or empty.", 400);
        }

        // Validate amount
        if (transactionDTO.getAmount() == null || transactionDTO.getAmount() <= 0) {
            throw new CategoryValidationException("Transaction amount must be greater than zero.", 400);
        }

        // Validate left account
        boolean leftAccountExists = categoryRepository.existsByNameAndTypeAndUserId(
                transactionDTO.getLeftCategory(),
                CategoryType.ACCOUNT,
                userId);

        if (!leftAccountExists) {
            throw new CategoryValidationException("The left account '" + transactionDTO.getLeftCategory() +
                    "' does not exist or is not of type 'ACCOUNT' for the user.", 404);
        }

        // Validate right account for TRANSFER
        if (transactionDTO.getType() == TransactionType.TRANSFER) {
            boolean rightAccountExists = categoryRepository.existsByNameAndTypeAndUserId(
                    transactionDTO.getRightCategory(),
                    CategoryType.ACCOUNT,
                    userId);

            if (!rightAccountExists) {
                throw new CategoryValidationException("The right account '" + transactionDTO.getRightCategory() +
                        "' does not exist or is not of type 'ACCOUNT' for the user.", 404);
            }
        }

        System.out.println("Account validation completed.");
    }

    private User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
    }

    private Category fetchCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID " + categoryId));
    }

    private Transaction initializeTransaction(TransactionDTO transactionDTO, User user, Category category) {
        System.out.println("Initializing transaction...");

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setLeftCategory(transactionDTO.getLeftCategory());
        transaction.setRightCategory(transactionDTO.getRightCategory());
        transaction.setType(transactionDTO.getType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setCreatedAt(transactionDTO.getCreatedAt());
        transaction.setCategory(category);
        transaction.setStatus(TransactionStatus.FAILED); // Default to FAILED initially

        System.out.println("Transaction initialized.");
        return transaction;
    }

    private void handleAccountBalanceUpdates(TransactionDTO transactionDTO, Long userId, Category category) {
        System.out.println("Updating account balances...");

        if (transactionDTO.getType() == TransactionType.INCOME) {
            updateAccountAmount(transactionDTO.getLeftCategory(), userId, transactionDTO.getAmount(), true);
        } else if (transactionDTO.getType() == TransactionType.EXPENSE) {
            validateSufficientFunds(category, transactionDTO.getAmount());
            updateAccountAmount(transactionDTO.getLeftCategory(), userId, transactionDTO.getAmount(), false);
        } else if (transactionDTO.getType() == TransactionType.TRANSFER) {
            validateSufficientFunds(category, transactionDTO.getAmount());
            updateAccountAmount(transactionDTO.getLeftCategory(), userId, transactionDTO.getAmount(), false);
            updateAccountAmount(transactionDTO.getRightCategory(), userId, transactionDTO.getAmount(), true);
        }

        System.out.println("Account balances updated.");
    }

    private void validateSufficientFunds(Category category, Double amount) {
        if (category.getBalance() - amount < 0) {
            throw new InsufficientFundsException("Insufficient funds. The balance cannot go below zero.");
        }
    }

    private void handleTransactionFailure(Transaction transaction, String errorMessage) {
        transaction.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(transaction);
        System.err.println("Transaction failed: " + errorMessage);
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getLeftCategory(),
                transaction.getRightCategory(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getCategory().getId()
        );
    }

    // Method to update the money account amount
    private void updateAccountAmount(String accountName, Long userId, Double amount, boolean isAdd) {
        // Find the account by name and type
        Category category = categoryRepository.findByNameAndTypeAndUserId(accountName, CategoryType.ACCOUNT, userId)
                .orElseThrow(() -> new RuntimeException("Account not found with name: " + accountName));

        System.out.println("Current amount: " + category.getBalance() + ". Amount to update: " + amount);

        // Check if balance is becoming negative on subtracting
        if (!isAdd && category.getBalance() - amount < 0) {
            throw new InsufficientFundsException("Insufficient funds. The balance cannot go below zero.");
        }

        // If adding, increase balance; if subtracting, decrease balance
        if (isAdd) {
            category.setBalance(category.getBalance() + amount);
        } else {
            category.setBalance(category.getBalance() - amount);
        }

        // Save the updated account balance
        categoryRepository.save(category);
        System.out.println("Account updated successfully.");
    }

    // Retrieve all transactions for the authenticated user
    public List<TransactionDTO> getAllTransactions(String authHeader) {
        // Decode user ID from the authorization header
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Retrieve transactions for the user
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // Convert entities to DTOs
        return transactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }
}
