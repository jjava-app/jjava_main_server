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

    // ✅ username으로 탐색 (KAKAO_..., NAVER_..., GOOGLE_... 매칭용)
    public Optional<User> findByUsername(String username) {
        var list = em.createQuery(
                        "select u from User u where u.username = :un", User.class)
                .setParameter("un", username)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    // ✅ email로 탐색 (동의한 경우 기존 계정 연동)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        var list = em.createQuery(
                        "select u from User u where lower(u.email) = :em", User.class)
                .setParameter("em", email.toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    // (선택) 존재 여부 체크가 필요할 때
    public boolean existsByUsername(String username) {
        Long cnt = em.createQuery(
                        "select count(u) from User u where u.username = :un", Long.class)
                .setParameter("un", username)
                .getSingleResult();
        return cnt > 0;
    }

}
