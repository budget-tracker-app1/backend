package com.my_sample_project.BudgetApp.dto.transaction;

import java.util.Date;

public interface TransactionProjection {
    Integer getId();
    Long getUserId();
    String getAccount();
    String getIncome();
    Double getAmount();
    String getDescription();
    Date getCreatedAt();
}
