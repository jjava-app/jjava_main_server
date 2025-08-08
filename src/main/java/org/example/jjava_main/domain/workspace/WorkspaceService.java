package org.example.jjava_main.domain.workspace;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception403;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main._core.util.AuthUtil;
import org.example.jjava_main.dto.WorkspaceRequest;
import org.example.jjava_main.dto.WorkspaceResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public WorkspaceResponse.CreateDTO workspaceCreate(Integer userId) {
        Workspace workspace = new Workspace().builder().title("새 워크스페이스").userId(userId).build();
        Workspace workspacePS = workspaceRepository.create(workspace);
        return new WorkspaceResponse.CreateDTO(workspacePS);
    }

    public List<Workspace> workspaceList(Integer userId) {
        List<Workspace> workspaceList = workspaceRepository.findAllbyUserId(userId);
        return workspaceList;
    }

    public Workspace workspaceDetail(Integer workspaceId, Integer userId) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        if(!workspacePS.getUserId().equals(userId)) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");
        return workspacePS;
    }

    @Transactional
    public void workspaceDelete(Integer workspaceId, Integer userId) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        // 워크스페이스 소유자 id랑 로그인한 유저 id 비일치 시 403 에러 발생 코드 추가 필요
        if(!workspacePS.getUserId().equals(userId)) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");
        workspaceRepository.deleteById(workspaceId);
    }

    @Transactional
    public WorkspaceResponse.DTO workspaceUpdate(Integer workspaceId, WorkspaceRequest.UpdateDTO reqDTO, Integer userId) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        // 워크스페이스 소유자 id랑 로그인한 유저 id 비일치 시 403 에러 발생 코드 추가 필요
        if(!workspacePS.getUserId().equals(userId)) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");
        workspacePS.update(reqDTO.getTitle(), reqDTO.getSerializedJson(), reqDTO.getBlockExtensionJson());
        return new WorkspaceResponse.DTO(workspacePS);
    }
}
