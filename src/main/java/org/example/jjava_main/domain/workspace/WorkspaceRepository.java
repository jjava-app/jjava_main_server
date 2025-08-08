package org.example.jjava_main.domain.workspace;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class WorkspaceRepository {
    private final EntityManager em;

    public void create(Workspace workspace) {
        em.persist(workspace);
    }

    public List<Workspace> findAll() {
        return em.createQuery("select w from Workspace w", Workspace.class).getResultList();
    }

    public Workspace findWorkspaceById(Integer workspaceId) {
        return em.find(Workspace.class, workspaceId);
    }

    public void deleteById(Integer workspaceId) {
        em.remove(em.find(Workspace.class, workspaceId));
    }
}
