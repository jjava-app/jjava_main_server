package org.example.jjava_main.domain.compile;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solved_question_tb")
public class SolvedQuestion {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private String AiComment;
    private String serializedJson;
    private String blockExtensionJson;

    @Builder
    public SolvedQuestion(Integer id, Integer userId, Integer questionId, String AiComment, String serializedJson, String blockExtensionJson) {
        this.id = id;
        this.userId = userId;
        this.questionId = questionId;
        this.AiComment = AiComment;
        this.serializedJson = serializedJson;
        this.blockExtensionJson = blockExtensionJson;
    }
}
