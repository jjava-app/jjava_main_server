package org.example.jjava_main.domain.question;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "question_tb")
public class Question {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Enumerated(EnumType.STRING)
    private QuestionType type;
    private String title;
    private String content;
    private String testVariable;
    private String testAnswer;
    private Integer score;

    @Builder
    public Question(Integer id, QuestionType type, String title, String content, String testVariable, String testAnswer, Integer score) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.testVariable = testVariable;
        this.testAnswer = testAnswer;
        this.score = score;
    }

    public void update(QuestionType type, String title, String content, String testVariable, String testAnswer) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.testVariable = testVariable;
        this.testAnswer = testAnswer;
    }
}
