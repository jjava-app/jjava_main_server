package org.example.jjava_main.domain.question;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QuestionRepository {
    private final EntityManager em;


    // 문제 단건 조회
    public Question findById(Integer id) {
        return em.find(Question.class, id);
    }

    // 문제 저장
    public Question save(Question question) {
        em.persist(question);
        return question;
    }


}
