package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public List<User> findAllOrderById(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select u from User u order by u.id", User.class);
        else query = em.createQuery("select u from User u order by u.id desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByName(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select u from User u order by u.username", User.class);
        else query = em.createQuery("select u from User u order by u.username desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByEmail(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select u from User u order by u.email", User.class);
        else query = em.createQuery("select u from User u order by u.email desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByScore(int page, int sort) {
        Query query;
        if(sort == 0) query = em.createQuery("select u from User u order by u.score", User.class);
        else query = em.createQuery("select u from User u order by u.score desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public Long getTotalCount() {
        return em.createQuery("select count(u) from User u", Long.class).getSingleResult();
    }
}
