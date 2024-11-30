package com.my_sample_project.BudgetApp.dto.transaction;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {

    private Integer id;
    private Long user_id;
    private String account;
    private String income;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private Integer category_id;

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(
            Integer id,
            Long user_id,
            String account,
            String income,
            Double amount,
            String description,
            LocalDateTime createdAt,
            Integer category_id
    ) {
        this.id = id;
        this.user_id = user_id;
        this.account = account;
        this.income = income;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.category_id = category_id;
    }
}
