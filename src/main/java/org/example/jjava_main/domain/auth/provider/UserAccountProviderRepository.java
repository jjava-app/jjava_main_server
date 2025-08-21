package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAccountProviderRepository {
    @PersistenceContext
    private final EntityManager em;

    public UserAccountProvider save(UserAccountProvider link) {
        em.persist(link);
        return link;
    }

    public Optional<UserAccountProvider> findLink(ProviderType type, String providerUserId) {
        List<UserAccountProvider> list = em.createQuery("""
                        select uap
                        from UserAccountProvider uap
                        join uap.provider p
                        where p.providerType = :type
                          and uap.providerUserId = :pid
                        """, UserAccountProvider.class)
                .setParameter("type", type)
                .setParameter("pid", providerUserId)
                .getResultList();
        return list.stream().findFirst();
    }

    public long countLinks(ProviderType type, String providerUserId) {
        return em.createQuery("""
                        select count(uap)
                        from UserAccountProvider uap
                        join uap.provider p
                        where p.providerType = :type
                          and uap.providerUserId = :pid
                        """, Long.class)
                .setParameter("type", type)
                .setParameter("pid", providerUserId)
                .getSingleResult();
    }

    public List<UserAccountProvider> findAllByUserId(Integer userId) {
        return em.createQuery("""
                        select uap
                        from UserAccountProvider uap
                        where uap.user.id = :userId
                        """, UserAccountProvider.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}