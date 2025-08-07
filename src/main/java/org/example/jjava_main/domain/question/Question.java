package org.example.jjava_main.domain.question;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "question_tb")
public class Question {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private QuestionType type;
    private String title;
    private String content;
    private String testVariable;
    private String testAnswer;

    @Builder
    public Question(Integer id, QuestionType type, String title, String content, String testVariable, String testAnswer) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.testVariable = testVariable;
        this.testAnswer = testAnswer;
    }
}
