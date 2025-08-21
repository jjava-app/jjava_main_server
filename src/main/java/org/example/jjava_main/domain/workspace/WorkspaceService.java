package org.example.jjava_main.domain.workspace;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception403;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main._core.util.AuthUtil;
import org.example.jjava_main.domain.block.BlockLibrary;
import org.example.jjava_main.domain.user.User;
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
    public WorkspaceResponse.CreateDTO workspaceCreate(User user) {


        Workspace workspace = new Workspace().builder().title("새 워크스페이스").user(user).build();
        Workspace workspacePS = workspaceRepository.create(workspace);
        BlockLibrary blockLibrary = new BlockLibrary().builder().user(user).build();
        BlockLibrary blockLibraryPS = workspaceRepository.createBlockLibrary(blockLibrary);

        return new WorkspaceResponse.CreateDTO(workspacePS);
    }

    public WorkspaceResponse.ListDTO workspaceList(User user) {
        List<Workspace> workspaceList = workspaceRepository.findAllbyUserId(user.getId());
        return new WorkspaceResponse.ListDTO(workspaceList);
    }

    public WorkspaceResponse.DTO workspaceDetail(Integer workspaceId, User user) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        if(!workspacePS.getUser().getId().equals(user.getId())) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");

        BlockLibrary blockLibraryPS = workspaceRepository.findBlockLibraryByUserId(user.getId())
                .orElseGet(null);
        if(blockLibraryPS != null && !blockLibraryPS.getUser().getId().equals(user.getId())) throw new Exception403("해당 유저의 라이브러리가 아닙니다.");

        return new WorkspaceResponse.DTO(workspacePS, blockLibraryPS);
    }

    @Transactional
    public void workspaceDelete(Integer workspaceId, User user) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        // 워크스페이스 소유자 id랑 로그인한 유저 id 비일치 시 403 에러 발생 코드 추가 필요
        if(!workspacePS.getUser().getId().equals(user.getId())) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");
        workspaceRepository.deleteById(workspaceId);
    }

    @Transactional
    public WorkspaceResponse.DTO workspaceUpdate(Integer workspaceId, WorkspaceRequest.UpdateDTO reqDTO, User user) {
        Workspace workspacePS = workspaceRepository.findWorkspaceById(workspaceId)
                .orElseThrow(() -> new Exception404("해당 워크스페이스가 존재하지 않습니다."));
        // 워크스페이스 소유자 id랑 로그인한 유저 id 비일치 시 403 에러 발생 코드 추가 필요
        if(!workspacePS.getUser().getId().equals(user.getId())) throw new Exception403("해당 워크스페이스의 소유자가 아닙니다.");
        workspacePS.update(reqDTO.getTitle(), reqDTO.getSerializedJson());

        BlockLibrary blockLibraryPS = workspaceRepository.findBlockLibraryByUserId(user.getId())
                .orElseGet(null);
        if(blockLibraryPS != null && !blockLibraryPS.getUser().getId().equals(user.getId())) throw new Exception403("해당 유저의 라이브러리가 아닙니다.");
        blockLibraryPS.update(reqDTO.getLibraryJson());

        return new WorkspaceResponse.DTO(workspacePS, blockLibraryPS);
    }
}
