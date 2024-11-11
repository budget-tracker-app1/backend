package com.my_sample_project.BudgetApp.model;

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
    @Column(name = "category_id", columnDefinition = "INT")
    private Integer categoryId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "type", columnDefinition = "ENUM('income', 'expense')", nullable = false)
    private String type;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
