package com.my_sample_project.BudgetApp.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountAmountUpdateDTO {
    private String name;
    private Long userId;
    private Double amount;
}
