package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public List<User> findAllOrderById(int page, int sort) {
        Query query;
        if (sort == 0) query = em.createQuery("select u from User u order by u.id", User.class);
        else query = em.createQuery("select u from User u order by u.id desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByName(int page, int sort) {
        Query query;
        if (sort == 0) query = em.createQuery("select u from User u order by u.username", User.class);
        else query = em.createQuery("select u from User u order by u.username desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByEmail(int page, int sort) {
        Query query;
        if (sort == 0) query = em.createQuery("select u from User u order by u.email", User.class);
        else query = em.createQuery("select u from User u order by u.email desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public List<User> findAllOrderByScore(int page, int sort) {
        Query query;
        if (sort == 0) query = em.createQuery("select u from User u order by u.score", User.class);
        else query = em.createQuery("select u from User u order by u.score desc", User.class);

        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    public Long getTotalCount() {
        return em.createQuery("select count(u) from User u", Long.class).getSingleResult();
    }

    public User save(User user) {
        if (user.getId() == null) { // 새 엔티티
            em.persist(user);
            return user;
        } else {
            return em.merge(user);   // 업데이트
        }
    }

    public Optional<User> findByUsername(String username) {
        var list = em.createQuery(
                        "select u from User u where u.username = :un", User.class)
                .setParameter("un", username)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        var list = em.createQuery(
                        "select u from User u where lower(u.email) = :em", User.class)
                .setParameter("em", email.toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    public int findRankByScoreAndId(Integer score, Integer id) {
        String sql = """
                  SELECT COUNT(*) + 1
                  FROM user_tb
                  WHERE score > :score
                     OR (score = :score AND id < :id)
                """;
        Number n = (Number) em.createNativeQuery(sql)
                .setParameter("score", score)
                .setParameter("id", id)
                .getSingleResult();
        return n.intValue();

    }
}
