package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProviderRepository {
    @PersistenceContext
    private final EntityManager em;

    public Optional<Provider> findByProviderType(ProviderType type) {
        List<Provider> list = em.createQuery(
                        "select p from Provider p where p.providerType = :type", Provider.class)
                .setParameter("type", type)
                .getResultList();
        return list.stream().findFirst();
    }

    public void persist(Provider provider) {
        em.persist(provider);
    }
}