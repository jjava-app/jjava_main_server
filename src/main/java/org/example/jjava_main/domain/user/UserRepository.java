package org.example.jjava_main.domain.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public User save(User u) { // todo: 테스트한다고 빨리 만들었는데 나중에 컨벤션 맞게 수정해서 만들기
        if (u.getId() == null) { // 새 엔티티
            em.persist(u);
            return u;
        } else {
            return em.merge(u);   // 업데이트
        }
    }

}
