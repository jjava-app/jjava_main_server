package org.example.jjava_main.domain.workspace;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.AuthUtil;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public String workspaceCreate() {
        /* 새 워크스페이스 생성
        * userId에는 현재 로그인한 유저의 id를 가져와야 함 (AuthUtil.getCurrentUserId())
        * 나머지 블록 정보는 update로 처리
        * */
        Workspace workspace = new Workspace().builder().title("새 워크스페이스").userId(1).build();
        workspaceRepository.create(workspace);
        return "created : " + workspace.getId();
    }

    public void workspaceList() {
    }

    public void workspaceDetail() {
    }

    @Transactional
    public void workspaceDelete() {
    }

    @Transactional
    public void workspaceUpdate() {
    }
}
