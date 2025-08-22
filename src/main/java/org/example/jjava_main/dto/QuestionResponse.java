package org.example.jjava_main.dto;

import lombok.Data;
import org.example.jjava_main.domain.compile.SolvedQuestion;
import org.example.jjava_main.domain.question.ProgressStatus;
import org.example.jjava_main.domain.question.Question;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class QuestionResponse {

    @Data
    public static class ListDTO {
        private Integer userId;
        private Integer totalCount;
        private Integer solvedCount;
        private List<QuestionDTO> questions;
        private List<Integer> solvedQuestionIds;

        @Data
        public static class QuestionDTO {
            private Integer questionId;
            private String questionType;
            private String title;

            public QuestionDTO(Integer questionId, String questionType, String title) {
                this.questionId = questionId;
                this.questionType = questionType;
                this.title = title;
            }
        }

        public ListDTO(Integer userId, Integer totalCount, Integer solvedCount, List<QuestionDTO> questions, List<Question> solvedQuestions) {
            this.userId = userId;
            this.totalCount = totalCount;
            this.solvedCount = solvedCount;
            this.questions = questions;
            this.solvedQuestionIds = solvedQuestions.stream().map(Question::getId).toList();
        }
    }

    @Data
    public static class DetailDTO {
        private Integer questionId;
        private String title;
        private String content;

        public DetailDTO(Integer questionId, String title, String content) {
            this.questionId = questionId;
            this.title = title;
            this.content = content;
        }
    }

    @Data
    public static class SolvedQuestionDetailDTO {
        private Integer questionId;
        private String title;
        private String content;
        private String AiComment;
        private String serializedJson;
        private String blockExtensionJson;
        private String createdAt;

        public SolvedQuestionDetailDTO(Question question, SolvedQuestion solvedQuestion) {
            this.questionId = question.getId();
            this.title = question.getTitle();
            this.content = question.getContent();
            this.AiComment = solvedQuestion.getAiComment();
            this.serializedJson = solvedQuestion.getSerializedJson();
            this.blockExtensionJson = solvedQuestion.getBlockExtensionJson();
            this.createdAt = solvedQuestion.getCreatedAt().toString();
        }
    }

    @Data
    public static class SolvedQuestionCreateDTO {
        private Integer solvedQuestionId;
        private Integer userId;
        private Integer questionId;
        private String serializedJson;
        private String blockExtensionJson;
        private ProgressStatus status;

        public SolvedQuestionCreateDTO(Integer solvedQuestionId, Integer userId, Integer questionId, String serializedJson, String blockExtensionJson, ProgressStatus status) {
            this.solvedQuestionId = solvedQuestionId;
            this.userId = userId;
            this.questionId = questionId;
            this.serializedJson = serializedJson;
            this.blockExtensionJson = blockExtensionJson;
            this.status = status;
        }
    }

    @Data
    public static class AdminListDTO {
        private List<Question> questions;
        private Integer page;
        private String order;
        private Integer sort;
        private Integer totalCount;

        public AdminListDTO(List<Question> questions, Integer page, String order, Integer sort, Integer totalCount) {
            this.questions = questions;
            this.page = page;
            this.order = order;
            this.sort = sort;
            this.totalCount = totalCount;
        }
    }

    @Data
    public static class DTO {
        private Integer questionId;
        private String questionType;
        private String title;
        private String content;

        public DTO(Question question) {
            this.questionId = question.getId();
            this.questionType = question.getType().toString();
            this.title = question.getTitle();
            this.content = question.getContent();
        }
    }
    @Data
    public static class SolvedQuestionDTO {
        private Integer solvedQuestionId;
        private Integer questionId;
        private String title;
        private String questionType;
        private Timestamp createdAt;

        public SolvedQuestionDTO(SolvedQuestion solvedQuestion) {
            this.solvedQuestionId = solvedQuestion.getId();
            this.questionId = solvedQuestion.getQuestion().getId();
            this.title = solvedQuestion.getQuestion().getTitle();
            this.questionType = solvedQuestion.getQuestion().getType().toString();
            this.createdAt = solvedQuestion.getCreatedAt();
        }
    }

    // 내가푼 문제 리스트
    @Data
    public static class SolvedQuestionListDTO {
        private Map<String, List<SolvedQuestionDTO>> groupedSolvedQuestions;


        public SolvedQuestionListDTO(List<SolvedQuestion> solvedQuestionList) {
            // SolvedQuestion → SolvedQuestionDTO 변환
            List<SolvedQuestionDTO> dtoList = solvedQuestionList.stream()
                    .map(SolvedQuestionDTO::new)
                    .collect(Collectors.toList());

            // questionType별로 그룹핑
            this.groupedSolvedQuestions = dtoList.stream()
                    .collect(Collectors.groupingBy(SolvedQuestionDTO::getQuestionType));
        }
    }

    @Data
    public static class HomeDTO {
        private List<SolvedQuestionDTO> sqList;

        public HomeDTO(List<SolvedQuestion> sqList) {
            List<SolvedQuestion> safe = (sqList == null) ? Collections.emptyList() : sqList;

            this.sqList = safe.stream()
                    .limit(3)
                    .map(SolvedQuestionDTO::new)
                    .toList();
        }
    }
}

