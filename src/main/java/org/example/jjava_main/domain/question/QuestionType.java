package org.example.jjava_main.domain.question;

public enum QuestionType {
    OPERATOR("연산자"),
    TEXT("텍스트"),
    CONDITIONAL("조건문"),
    LOOP("반복문"),
    ARRAY("배열");

    private final String name;

    QuestionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
