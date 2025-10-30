package com.edusmart.entity;

import com.edusmart.entity.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Question entity for assessment questions
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"assessment", "options"})
@ToString(exclude = {"assessment", "options"})
public class Question extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    @NotBlank(message = "Question text is required")
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 50)
    private QuestionType questionType;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal points;
    
    @Column(name = "marks")
    private Integer marks;
    
    @Column(name = "question_order")
    private Integer questionOrder;
    
    @Column(name = "order_number")
    private Integer orderNumber;
    
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;
    
    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("optionOrder ASC")
    private List<QuestionOption> options = new ArrayList<>();
}
