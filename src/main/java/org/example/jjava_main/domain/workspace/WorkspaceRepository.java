package org.example.jjava_main.domain.workspace;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.block.BlockLibrary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class WorkspaceRepository {
    private final EntityManager em;

    public Workspace create(Workspace workspace) {
        em.persist(workspace);
        return workspace;
    }

    public List<Workspace> findAllbyUserId(Integer userId) {
        return em.createQuery("select w from Workspace w where w.user.id =: userId", Workspace.class).setParameter("userId", userId).getResultList();
    }

    public Optional<Workspace> findWorkspaceById(Integer workspaceId) {
        return Optional.ofNullable(em.find(Workspace.class, workspaceId));
    }

    public void deleteById(Integer workspaceId) {
        em.remove(em.find(Workspace.class, workspaceId));
    }

    public Optional<BlockLibrary> findBlockLibraryByUserId(Integer userId) {
        return Optional.ofNullable(em.find(BlockLibrary.class, userId));
    }

    public BlockLibrary createBlockLibrary(BlockLibrary blockLibrary) {
        em.persist(blockLibrary);
        return blockLibrary;
    }
}
