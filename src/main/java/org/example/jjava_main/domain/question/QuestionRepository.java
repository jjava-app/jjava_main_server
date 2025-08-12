package org.example.jjava_main.domain.question;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.compile.SolvedQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public SolvedQuestion saveSolvedQuestion(SolvedQuestion solvedQuestion) {
        em.persist(solvedQuestion);
        return solvedQuestion;
    }

    // 문제 전체 조회 (id & title)
    public List<Question> findAll() {
        Query query = em.createQuery("select q from Question q");
        return query.getResultList();
    }

    // 푼 문제 id 조회
    public List<SolvedQuestion> findSolvedQuestionByUserId(Integer userId) {
        Query query = em.createQuery("select s from SolvedQuestion s where s.userId = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }


}
