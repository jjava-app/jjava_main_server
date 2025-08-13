package org.example.jjava_main.domain.question;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.compile.SolvedQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class QuestionRepository {
    private final EntityManager em;


    // 문제 단건 조회
    public Optional<Question> findById(Integer id) {
        return Optional.ofNullable(em.find(Question.class, id));
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
        Query query = em.createQuery("select s from SolvedQuestion s where s.user.id = :userId");
        query.setParameter("userId", userId);
        return query.getResultList();
    }


    public List<Question> findAllOrderById(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select q from Question q order by q.id", Question.class);
        else query = em.createQuery("select q from Question q order by q.id desc", Question.class);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<Question> findAllOrderByTitle(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select q from Question q order by q.title", Question.class);
        else query = em.createQuery("select q from Question q order by q.title desc", Question.class);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<Question> findAllOrderByType(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select q from Question q order by q.type", Question.class);
        else query = em.createQuery("select q from Question q order by q.type desc", Question.class);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public Long getTotalCount() {
        return em.createQuery("select count(q) from Question q", Long.class).getSingleResult();
    }

    public void deleteById(Integer id) {
        em.remove(em.find(Question.class, id));
    }
}
