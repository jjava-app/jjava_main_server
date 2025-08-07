package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    @Transactional
    public void updateLevelById(UserLevel level, Integer id) {
        String q = "UPDATE User u SET u.level = :level WHERE u.id = :id";
        em.createQuery(q)
                .setParameter("level", level)
                .setParameter("id", id)
                .executeUpdate();
    }
}
