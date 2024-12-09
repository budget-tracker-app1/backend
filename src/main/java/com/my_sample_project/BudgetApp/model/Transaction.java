package com.my_sample_project.BudgetApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_TRANSACTION")
    )
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CATEGORY_TRANSACTIONS")
    )
    private Category category;

    @Column(name = "left_category", nullable = false)
    private String leftCategory;

    @Column(name = "right_category", nullable = false)
    private String rightCategory;

    @Column(name = "type", columnDefinition = "ENUM('INCOME', 'EXPENSE', 'TRANSFER')", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", columnDefinition = "ENUM('SUCCESS', 'FAILED')")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
