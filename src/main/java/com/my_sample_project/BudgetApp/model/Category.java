package com.my_sample_project.BudgetApp.model;

import com.my_sample_project.BudgetApp.exception.CategoryValidationException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INT")
    private Integer id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "type", columnDefinition = "ENUM('ACCOUNT', 'INCOME', 'EXPENSE')", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "color", length = 32)
    private String color;

    @Column(name = "balance", nullable = true)
    private Double balance;

    // Custom validation logic
    @PrePersist
    @PreUpdate
    private void validateColorAndAmount() {
        if (CategoryType.EXPENSE.equals(type) && (color == null || color.isEmpty())) {
            throw new CategoryValidationException(
                    "Color is required for 'EXPENSE' type categories.",
                    400
            );
        }

        // Set amount to 0.00 for ACCOUNT type categories if it's not already set
        if (CategoryType.ACCOUNT.equals(type) && balance == null) {
            balance = 0.00;
        }
    }
}
