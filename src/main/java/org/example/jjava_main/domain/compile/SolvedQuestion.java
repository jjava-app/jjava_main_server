package org.example.jjava_main.domain.compile;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.user.User;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solved_question_tb")
public class SolvedQuestion {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    private String AiComment;
    private String serializedJson;
    private String blockExtensionJson;

    @Builder
    public SolvedQuestion(Integer id, User user, Question question, String AiComment, String serializedJson, String blockExtensionJson) {
        this.id = id;
        this.user = user;
        this.question = question;
        this.AiComment = AiComment;
        this.serializedJson = serializedJson;
        this.blockExtensionJson = blockExtensionJson;
    }
}
