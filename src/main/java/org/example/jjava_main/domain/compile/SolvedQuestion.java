package org.example.jjava_main.domain.compile;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jjava_main.domain.question.ProgressStatus;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

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

    @CreationTimestamp
    private Timestamp createdAt;

    private String AiComment;
    private String serializedJson;
    private String blockExtensionJson;

    @Enumerated(EnumType.STRING)
    private ProgressStatus progressStatus;

    @Builder
    public SolvedQuestion(Integer id, User user, Question question, String AiComment, String serializedJson, String blockExtensionJson, ProgressStatus progressStatus, Timestamp createdAt) {
        this.id = id;
        this.user = user;
        this.question = question;
        this.AiComment = AiComment;
        this.serializedJson = serializedJson;
        this.blockExtensionJson = blockExtensionJson;
        this.progressStatus = progressStatus;
        this.createdAt = createdAt;

    }

    // 부분 업데이트 (null은 유지)
    public void updateJson(String serializedJson, String blockExtensionJson) {
        if (serializedJson != null) this.serializedJson = serializedJson;
        if (blockExtensionJson != null) this.blockExtensionJson = blockExtensionJson;
    }

    public void updateProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public void updateAiComment(String AiComment) {
        this.AiComment = AiComment;
    }
}
