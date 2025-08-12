package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
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

    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery(
                            "select u from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * 닉네임이 존재하면 false
     * 닉네임이 존재하지 않으면 true
     */
    public Optional<User> findByUsername(String nickname) {
        try {
            User user = em.createQuery(
                            "select u from User u where u.username = :nickname", User.class)
                    .setParameter("nickname", nickname)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public int findRankByScoreAndId(Integer score, Integer id) {
        String sql = """
                  SELECT COUNT(*) + 1
                  FROM user_tb
                  WHERE score > :score
                """;
        Number n = (Number) em.createNativeQuery(sql)
                .setParameter("score", score)
                .getSingleResult();
        return n.intValue();

    }
    public UserResponse.UserUpdateDTO updateUser(Integer id,String username, String email){
        Query query = em.createQuery("update User u set u.email = :email,u.username = :username where u.id = :id");
        query.setParameter("email", email);
        query.setParameter("username", username);
        query.setParameter("id",id);
        int updateCount = query.executeUpdate();

        if(updateCount == 0) {
            throw new RuntimeException("업데이트 실패: 해당 유저 없음");
        }

        // 업데이트 후 변경된 유저 엔티티 다시 조회
        User updatedUser = em.createQuery("select u from User u where u.id = :id", User.class)
                .setParameter("id", id).getSingleResult();

        return new UserResponse.UserUpdateDTO(updatedUser);
    }

}
