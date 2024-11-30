package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.dto.account.AccountAmountUpdateDTO;
import com.my_sample_project.BudgetApp.dto.category.CategoryDTO;
import com.my_sample_project.BudgetApp.dto.transaction.TransactionDTO;
import com.my_sample_project.BudgetApp.model.Category;
import com.my_sample_project.BudgetApp.model.CategoryType;
import com.my_sample_project.BudgetApp.model.Transaction;
import com.my_sample_project.BudgetApp.model.User;
import com.my_sample_project.BudgetApp.repository.CategoryRepository;
import com.my_sample_project.BudgetApp.repository.TransactionRepository;
import com.my_sample_project.BudgetApp.repository.UserRepository;
import com.my_sample_project.BudgetApp.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            JwtUtil jwtUtil, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // Helper method to create a transaction and return the corresponding TransactionDTO
    private TransactionDTO saveTransactionHelper(Long userId, TransactionDTO transactionDTO) {
        try {
            System.out.println("Validating account...");
            // Validate that the account exists in categories as an ACCOUNT type for the given user
            boolean accountExists = categoryRepository.existsByNameAndTypeAndUserId(
                    transactionDTO.getAccount(),
                    CategoryType.ACCOUNT,
                    userId
            );

            if (!accountExists) {
                throw new RuntimeException("The account '" + transactionDTO.getAccount() +
                        "' does not exist or is not of type 'ACCOUNT' for the user.");
            }

            System.out.println("Account validated. Creating transaction...");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            Category category = categoryRepository.findById(transactionDTO.getCategory_id())
                    .orElseThrow(() -> new RuntimeException(
                            "Category not found with ID " + transactionDTO.getCategory_id())
                    );

            // Create a new transaction
            Transaction transaction = new Transaction();

            // Set properties from DTO
            transaction.setUser(user);
            transaction.setAccount(transactionDTO.getAccount());
            transaction.setIncome(transactionDTO.getIncome());
            transaction.setAmount(transactionDTO.getAmount());
            transaction.setDescription(transactionDTO.getDescription());
            transaction.setCreatedAt(transactionDTO.getCreatedAt());
            transaction.setCategory(category);

            // Save the transaction in the database
            transaction = transactionRepository.save(transaction);

            System.out.println("Transaction saved. Updating account amount...");
            // Update the associated account's amount
            AccountAmountUpdateDTO accountAmountUpdateDTO = new AccountAmountUpdateDTO(
                    transactionDTO.getAccount(),
                    userId,
                    transactionDTO.getAmount()
            );
            updateAccountAmount(accountAmountUpdateDTO);

            System.out.println("Account amount updated. Returning transaction DTO...");
            // Return the saved transaction as a DTO
            return new TransactionDTO(
                    transaction.getId(),
                    transaction.getUser().getId(),
                    transaction.getAccount(),
                    transaction.getIncome(),
                    transaction.getAmount(),
                    transaction.getDescription(),
                    transaction.getCreatedAt(),
                    transaction.getCategory().getId()
            );
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("An error occurred while saving the transaction", e);
        }
    }

    // Method to update the money account amount
    private void updateAccountAmount(AccountAmountUpdateDTO accountAmountUpdateDTO) {
        try {
            // Find the account by name and type
            Category category = categoryRepository.findByNameAndTypeAndUserId(
                    accountAmountUpdateDTO.getName(),
                    CategoryType.ACCOUNT,
                    accountAmountUpdateDTO.getUserId()
            ).orElseThrow(() -> new RuntimeException("Account not found with name: " + accountAmountUpdateDTO.getName()));

            System.out.println("Current amount: " + category.getBalance() + ". Adding: " + accountAmountUpdateDTO.getAmount());

            // Update the account amount
            category.setBalance(category.getBalance() + accountAmountUpdateDTO.getAmount());

            // Save the updated account
            categoryRepository.save(category);

            System.out.println("Account updated successfully.");
        } catch (Exception e) {
            System.err.println("Error in updateAccountAmount: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("An error occurred while updating the account amount", e);
        }
    }

    // Save a new transaction for the authenticated user
    public TransactionDTO saveTransaction(String authHeader, TransactionDTO transactionDTO) {
        // Extract userId from token
        Long userId = jwtUtil.extractUserIdFromToken(authHeader);

        // Use helper method to create and save the transaction
        return saveTransactionHelper(userId, transactionDTO);
    }
}
