package com.my_sample_project.BudgetApp.model;

import com.my_sample_project.BudgetApp.exception.CategoryValidationException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
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

    @Column(name = "type", columnDefinition = "ENUM('income', 'expense')", nullable = false)
    private String type;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "color", length = 32)
    private String color;

//     Custom validation logic
    @PrePersist
    @PreUpdate
    private void validateColor() {
        if ("expense".equals(type) && (color == null || color.isEmpty())) {
            throw new CategoryValidationException(
                "Color is required for 'expense' type categories.",
                400
            );
        }
    }
}
