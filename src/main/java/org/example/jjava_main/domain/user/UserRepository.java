package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.util.JwtUtil;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

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

    public void save(User user) {
        em.persist(user);
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
}
