package org.example.jjava_main.domain.workspace;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class WorkspaceRepository {
    private final EntityManager em;

    public void create(Workspace workspace) {
        em.persist(workspace);
    }
}
