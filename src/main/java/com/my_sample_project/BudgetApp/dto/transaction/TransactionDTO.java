package com.my_sample_project.BudgetApp.dto.transaction;

import com.my_sample_project.BudgetApp.model.TransactionStatus;
import com.my_sample_project.BudgetApp.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {

    private Integer id;
    private Long user_id;
    private String leftCategory;
    private String rightCategory;
    private TransactionType type;
    private Double amount;
    private String description;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private Integer category_id;

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(
            Integer id,
            Long user_id,
            String leftCategory,
            String rightCategory,
            TransactionType type,
            Double amount,
            String description,
            TransactionStatus status,
            LocalDateTime createdAt,
            Integer category_id
    ) {
        this.id = id;
        this.user_id = user_id;
        this.leftCategory = leftCategory;
        this.rightCategory = rightCategory;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.category_id = category_id;
    }
}
