package com.example.teampandanback.service;

import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.file.FileRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.dto.bookmark.response.BookmarkDetailForProjectListDto;
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.project.request.ProjectInvitedRequestDto;
import com.example.teampandanback.dto.project.request.ProjectRequestDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.*;
import com.example.teampandanback.dto.user.CrewDetailForProjectListDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.AESEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.TabExpander;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final NoteRepository noteRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final AESEncryptor aesEncryptor;
    private final NoteService noteService;
    private final CommentService commentService;

    // 사이드바에 들어갈 Project 최대 갯수
    private final int SIDEBAR_SIZE = 5;


    // Project 목록 조회
    @Transactional(readOnly = true)
    public List<ProjectEachResponseDto> readProjectList(User currentUser) {
        // 유저가 참여하고 있는 프로젝트 목록 조회
        List<UserProjectMapping> userProjectList = userProjectMappingRepository.findByUserId(currentUser.getUserId());
        // 유저가 참여하고 있는 프로젝트 id의 목록
        List<Long> projectIdList = userProjectList.stream()
                .map(userProjectMapping -> userProjectMapping.getProject().getProjectId())
                .collect(Collectors.toList());
        // 유저가 참여하고 있는 프로젝트들의 세부정보 조회
        List<ProjectDetailForProjectListDto> projectDetail = projectRepository.findProjectDetailForProjectList(projectIdList);

        // 유저가 참여하고 있는 프로젝트들의 크루정보 조회
        List<CrewDetailForProjectListDto> rawCrewList =
                userProjectMappingRepository.findCrewDetailForProjectList(projectIdList);

        // 유저가 참여하고 있는 프로젝트들의 크루정보 정렬
        Map<Long, ArrayList<CrewDetailForProjectListDto>> crewMap = new HashMap<>();
        projectIdList.forEach(projectId -> crewMap.put(projectId, new ArrayList<>()));

        for (CrewDetailForProjectListDto crew : rawCrewList) {
            crewMap.get(crew.getProjectId()).add(crew);
        }

        // 유저가 참여하고 있는 프로젝트들의 북마크 정보 조회
        List<BookmarkDetailForProjectListDto> bookmarkCountList = bookmarkRepository.findBookmarkCountByProject(projectIdList, currentUser.getUserId());
        Map<Long, Long> bookmarkMap = new HashMap<>();
        bookmarkCountList.forEach(bookmark -> bookmarkMap.put(bookmark.getProjectId(), bookmark.getBookmarkCount()));

        return projectDetail.stream()
                .map(project ->
                        ProjectEachResponseDto
                                .of(project, userProjectList, crewMap.get(project.getProjectId()), bookmarkMap.get(project.getProjectId()))
                ).collect(Collectors.toList());
    }

    // Project 생성
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto, User currentUser) {
        // 사용자 조회
        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ApiRequestException("유저가 아니므로 프로젝트를 생성할 수 없습니다."));


        //프로젝트에 참여한 총 갯수
        Long theNumberOfCrewInvitedToProjects = userProjectMappingRepository.countByUser(user);

        Long upperBound = 10L;
        if (theNumberOfCrewInvitedToProjects >= upperBound) {
            throw new ApiRequestException("프로젝트에 이미 " + upperBound + "개 참여하였습니다.");
        }


        // 프로젝트 생성하고 저장
        Project project = projectRepository.save(Project.toEntity(requestDto));

        // 유저-프로젝트 테이블에도 저장
        UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project)
                .build();
        userProjectMappingRepository.save(userProjectMapping);

        return ProjectResponseDto.fromEntity(project);
    }

    // Project 수정
    @Transactional
    public ProjectDetailResponseDto updateProject(Long projectId, ProjectRequestDto requestDto, User currentUser) {

        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserIdAndProjectIdJoin(currentUser.getUserId(), projectId);

        if (!userProjectMapping.getRole().equals(UserProjectRole.OWNER)) {
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }
        Project project = userProjectMapping.getProject();
        project.update(requestDto);

        ProjectDetailResponseDto responseDto = ProjectDetailResponseDto.builder()
                .projectId(project.getProjectId())
                .detail(project.getDetail())
                .title(project.getTitle())
                .role(userProjectMapping.getRole())
                .build();

        responseDto.updateCrewCount(userProjectMappingRepository.findCountProjectMember(project.getProjectId()));

        return responseDto;
    }

    // Project 삭제
    @Transactional
    public ProjectDeleteResponseDto deleteProject(Long projectId, User currentUser) {
        Optional<UserProjectMapping> userProjectMapping = userProjectMappingRepository.findByUserIdAndProjectId(currentUser.getUserId(), projectId);

        // Project의 OWNER 권한 확인
        if (!userProjectMapping.isPresent()) { //   우선 해당 프로젝트의 CREW이기라도 한지.
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        } else if (!userProjectMapping.get().getRole().equals(UserProjectRole.OWNER)) { //   우선 해당 프로젝트의 CREW이더라도, OWNER가 아닌지,
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }

        // 해당 Project 와 연관된 Note에 속한 파일 삭제
        fileRepository.deleteFileByProjectId(projectId);

        // 해당 Project 와 연관된 Note에 속한 코멘트 삭제
        commentRepository.deleteCommentByProjectId(projectId);

        // Bookmark 테이블에서 Note 와 연관된 북마크 삭제
        bookmarkRepository.deleteByProjectId(projectId);

        // 해당 Project 와 연관된 Note 삭제
        noteRepository.deleteByProjectId(projectId);

        // UserProjectMapping 테이블에서 삭제
        userProjectMappingRepository.deleteByProjectId(projectId);

        // Project 테이블에서 Project 삭제
        projectRepository.deleteById(projectId);

        return ProjectDeleteResponseDto.builder()
                .projectId(projectId)
                .build();
    }

    // Project 회원 조회
    @Transactional
    public ProjectCrewResponseDto readCrewList(Long projectId) {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiRequestException("회원을 조회할 프로젝트가 존재하지 않습니다."));

        // 해당 프로젝트에 연관된 유저 목록 조회
        List<UserProjectMapping> userProjectMapping = userProjectMappingRepository.findAllByProject(project);
        // 유저목록에 있는 유저들의 아이디, 이름 조회
        List<CrewResponseDto> crewResponseDtoList = userProjectMapping.stream().map(
                crew -> CrewResponseDto.builder()
                        .userId(crew.getUser().getUserId())
                        .userName(crew.getUser().getName())
                        .userPicture(crew.getUser().getPicture())
                        .build())
                .collect(Collectors.toList());

        return ProjectCrewResponseDto.builder()
                .crews(crewResponseDtoList)
                .projectId(projectId)
                .build();
    }

    // 초대 코드로 프로젝트 참여
    @Transactional
    public ProjectInvitedResponseDto invitedProject(ProjectInvitedRequestDto projectInvitedRequestDto, User currentUser) {

        String decodedString = null;
        Long decodedLong = null;
        try {
            decodedString = aesEncryptor.decrypt(projectInvitedRequestDto.getInviteCode());
            decodedLong = Long.parseLong(decodedString);
        } catch (NumberFormatException e) {
            log.info("복호화 된 프로젝트ID가 숫자가 아닙니다. 프로젝트ID = " + decodedString);
            throw new ApiRequestException("유효하지 않은 초대 코드입니다.");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiRequestException("유효하지 않은 초대 코드 입니다.");
        }

        User newCrew = userRepository.findById(currentUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Project invitedProject = projectRepository.findById(decodedLong).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 프로젝트입니다.")
        );

        //프로젝트에 참여한 총 갯수
        Long theNumberOfCrewInvitedToProjects = userProjectMappingRepository.countByUser(newCrew);

        Long upperBound = 10L;
        if (theNumberOfCrewInvitedToProjects >= upperBound) {
            throw new ApiRequestException("프로젝트에 이미 " + upperBound + "개 참여하였습니다.");
        }

        UserProjectMapping newCrewRecord = userProjectMappingRepository.findByUserAndProject(newCrew, invitedProject)
                .orElseGet(() -> UserProjectMapping.builder()
                        .userProjectRole(UserProjectRole.CREW)
                        .user(newCrew)
                        .project(invitedProject)
                        .build());
        userProjectMappingRepository.save(newCrewRecord);

        return ProjectInvitedResponseDto.builder()
                .projectId(decodedLong)
                .build();
    }

    // Project 초대코드 생성
    public ProjectInviteResponseDto inviteProject(Long projectId, User currentUser) {
        User inviteOfferUser = userRepository.findById(currentUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Project inviteProject = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 프로젝트입니다.")
        );

        boolean isUserIsMemberOfProject = userProjectMappingRepository.existsByUserAndProject(inviteOfferUser, inviteProject);

        if (!isUserIsMemberOfProject) {
            throw new ApiRequestException("유저가 프로젝트의 구성원이 아닌데, 초대코드를 생성하려 합니다.");
        }

        String encodedString = null;
        try {
            encodedString = aesEncryptor.encrypt(Long.toString(projectId));
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiRequestException("초대 코드 발급 중 오류가 발생하였습니다.");
        }

        return ProjectInviteResponseDto.builder()
                .inviteCode(encodedString)
                .build();
    }

    // Project 상세 조회
    @Transactional(readOnly = true)
    public ProjectDetailResponseDto readProjectDetail(User currentUser, Long projectId) {

        ProjectDetailResponseDto responseDto = userProjectMappingRepository
                .findProjectDetail(currentUser.getUserId(), projectId)
                .orElseThrow(() -> new ApiRequestException("해당 유저는 접근권한이 없는 프로젝트입니다."));

        responseDto.updateCrewCount(userProjectMappingRepository.findCountProjectMember(projectId));

        return responseDto;
    }

    // 사이드 바에 들어갈 Project 목록 조회(최대 sidebarSize 개)
    @Transactional(readOnly = true)
    public List<ProjectSidebarResponseDto> readProjectListSidebar(User currentUser) {

        return userProjectMappingRepository.findProjectListTopSize(currentUser.getUserId(), SIDEBAR_SIZE);
    }


    // Project 에서 탈퇴하기
    @Transactional
    public void leaveProject(Long projectId, User user) {
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserIdAndProjectId(user.getUserId(), projectId).orElseThrow(
                () -> new ApiRequestException("참여하지 않은 프로젝트에서 탈퇴하려 합니다.")
        );
        // 해당 유저가 OWNER 라면 탈퇴할 수 없다
        if (userProjectMapping.getRole().equals(UserProjectRole.OWNER)) {
            throw new ApiRequestException("프로젝트의 OWNER 는 프로젝트를 탈퇴할 수 없습니다!");
        }
        // 해당 프로젝트내에서 유저가 북마크 했던 기록 삭제
        bookmarkRepository.deleteByProjectIdAndUserId(projectId, user.getUserId());

        userProjectMappingRepository.deleteById(userProjectMapping.getSeq());
    }


    //Guide 프로젝트 생성
    @Transactional
    public ProjectResponseDto createGuideProject(User currentUser) {

        // 사용자 조회
        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ApiRequestException("유저가 아니므로 프로젝트를 생성할 수 없습니다."));


        //프로젝트에 참여한 총 갯수
        Long theNumberOfCrewInvitedToProjects = userProjectMappingRepository.countByUser(user);


        if (theNumberOfCrewInvitedToProjects != 0L){
            throw new ApiRequestException("유저가 이미 가이드 프로젝트나, 아무 프로젝트에 참여 했습니다.");
        }



        //=====================================================================================================================================================
        //프로젝트 2 생성
        Project project2 = projectRepository.save(Project.builder()
                .title("[샘플]공학설계 조별 과제")
                .detail("한국대학교 2학년 소프트웨어학과")
                .build()
        );
        UserProjectMapping userProjectMapping2 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project2)
                .build();
        userProjectMappingRepository.save(userProjectMapping2);
        //========PROCESSING
        Long targetId =noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("틀에 박힌걸 만들어도 좋으니 뎁스를 깊게 하는 것을 좋아한다는 소문이 있음 \uD83D\uDE02 \n\n" +
                        "홍길동 선배가 작년 공모전 수상자이니 물어보자 !")
                .deadline(LocalDate.now().plusDays(2).toString())
                .step(Step.STORAGE.toString())
                .title("교수님 성향 파악하기 - by 홍길동 선배")
                .build(), currentUser).getNoteId();
        commentService.createComment(targetId, currentUser, CommentCreateRequestDto.builder()
                .content("틀에 박힌것이 여도 좋으니, 무조건 depth를 깊게 하는 것을 좋아하신다고 함 ⭐⭐⭐").build());
        commentService.createComment(targetId, currentUser, CommentCreateRequestDto.builder()
                .content("저번에 A+받으신 분은 기타 \uD83C\uDFB8 치는 로봇만드셨다고 합니다! ").build());

        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("아이디어를 다같이 생각해와서, 취합 할 예정입니다 !")
                .deadline(LocalDate.now().minusDays(1).toString())
                .step(Step.PROCESSING.toString())
                .title("아이디어 브레인스토밍 \uD83E\uDD28")
                .build(), currentUser);
        //=======DONE
        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("기간, 분업 등등 어떤 식으로 조별 과제를 차근차근 해결할지, 고민해 봅시다.")
                .deadline(LocalDate.now().minusDays(2).toString())
                .step(Step.DONE.toString())
                .title("로드맵 짜기 \uD83D\uDDFA️")
                .build(), currentUser);

        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("사이즈 6X가 3개 필요합니다. 근데 2개밖에 없고, 고무는 벗겨져있음.")
                .deadline(LocalDate.now().plusDays(1).toString())
                .step(Step.STORAGE.toString())
                .title("로봇 키드 불량 - 교환")
                .build(), currentUser);
        //=====TOD
        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("우리가 배운 것을 따라간다는 것을 어필하면서 사고하면 좋을 것 같습니다.")
                .deadline(LocalDate.now().plusDays(3).toString())
                .step(Step.STORAGE.toString())
                .title("공학적 사고")
                .build(), currentUser);

        Long targetId2 = noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("자동차도 좋고, 다른 것에 대해서도 찾아오셔도 좋습니다.")
                .deadline(LocalDate.now().plusDays(3).toString())
                .step(Step.TODO.toString())
                .title("참고 영상 2개 찾아오기기")
                .build(), currentUser).getNoteId();

        commentService.createComment(targetId2, currentUser, CommentCreateRequestDto.builder()
                .content("2개는 너무 적고 3개는 어떤가요?").build());

        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("인당 3개")
                .deadline(LocalDate.now().plusDays(3).toString())
                .step(Step.TODO.toString())
                .title("소재 정하기")
                .build(), currentUser);


        //=========================STORAGE

        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("자동차? 자전거? 어떤 재료로 시작해야 될까? 고민해봅시다.")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("로봇 키트 vs 레고")
                .build(), currentUser);
        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("수업중에 설명하신 교재 p.132를 참고하여 수식을 만들면 좋아보임.")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("무계 계산")
                .build(), currentUser);
        noteService.createNote(project2.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("많을수록 만들기 어렵다고 생각됩니다. 접합 부위를 최대한 줄여주세요.")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("접합 부위 적을 수록 좋다")
                .build(), currentUser);


        //프로젝트 3 생성

        Project project3 = projectRepository.save(Project.builder()
                .title("[샘플] 여행 계획 \uD83D\uDEEB")
                .detail("여름 끝나기 전에 한번 놀러 가야지~!~!")
                .build()
        );
        UserProjectMapping userProjectMapping3 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project3)
                .build();
        userProjectMappingRepository.save(userProjectMapping3);

        Long targetId3 = noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("휴가 신청 꼭 미리 확정 짓기!!! \n\n" +
                        "그때가서 못가면,,, 환불따윈 없다.")
                .deadline(LocalDate.now().minusDays(1).toString())
                .step(Step.DONE.toString())
                .title("\uD83D\uDED1휴가 신청")
                .build(), currentUser).getNoteId();
        commentService.createComment(targetId3, currentUser, CommentCreateRequestDto.builder()
                .content("너희 못 가도 나는 간다!!\n" +
                        "휴가 신청 제대로 하고와!!").build());

        Long targetId4 = noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("편도기준 25000월 이하 가격이 오면 무조건 예약 ")
                .deadline(LocalDate.now().plusDays(2).toString())
                .step(Step.PROCESSING.toString())
                .title("\uD83D\uDEEB비행기 특가  확인중")
                .build(), currentUser).getNoteId();

        commentService.createComment(targetId4, currentUser, CommentCreateRequestDto.builder()
                .content("확인해라 매일매일!!!").build());

        Long targetId5 = noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("1. 숙박 비용 정하기\n" +
                        "2. 수영장 유무 정하기")
                .deadline(LocalDate.now().plusDays(3).toString())
                .step(Step.TODO.toString())
                .title("\uD83C\uDFF0 숙소 예약")
                .build(), currentUser).getNoteId();

        commentService.createComment(targetId5, currentUser, CommentCreateRequestDto.builder()
                .content("xx호텔 :  55만원/1박당\n" +
                        "xxx 글래스 : 13만원 / 1박당\n" +
                        "xx 게스트 하우스 : 프라이빗 2인실 6만원 /1박당").build());

        Long targetId6 = noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("1. 렌트카 업체 알아보기,\n" +
                        "2. 렌트카 차종 정하기 ( 세단, suv, 컨버터블)")
                .deadline(LocalDate.now().plusDays(3).toString())
                .step(Step.TODO.toString())
                .title("\uD83D\uDE97 렌트카 예약")
                .build(), currentUser).getNoteId();

        commentService.createComment(targetId6, currentUser, CommentCreateRequestDto.builder()
                .content("나는 오픈카 타고 싶다~❗❗").build());

        //=================================================================

        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("수영복 쇼핑 하기 ")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83C\uDF0A  수영복 쇼핑")
                .build(), currentUser);
        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("바다구경 하러가자")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83E\uDDDE\u200D♀️ 스킨스쿠버 할까?")
                .build(), currentUser);
        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("자동차도 좋고, 뭐도 좋고")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83C\uDF5C고기국수는 무조건 먹어야지")
                .build(), currentUser);
        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("흑돼지 맛집 리스트\n" +
                        "1. 돈사돈 - 섭지코지 근처, TV에 많이 나온 맛집!\n" +
                        "2. 칠돈가 - 공항 근처, 웨이팅이 많을 수 있다.")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83D\uDC37 흑돼지 먹으러 가고 싶다")
                .build(), currentUser);
        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("[xx이네 해산물장터] 돌문어 볶음 먹으러가기")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83D\uDC19돌문어볶음 먹으러 가는건 어때?")
                .build(), currentUser);
        noteService.createNote(project3.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("한라산 백록담 찍고 오자!!!")
                .deadline(LocalDate.now().plusDays(4).toString())
                .step(Step.STORAGE.toString())
                .title("\uD83D\uDDFB 한라산 등반 할까?")
                .build(), currentUser);












        // 프로젝트 생성하고 저장
        Project project = projectRepository.save(Project.builder()
                .title("판단 가이드 프로젝트")
                .detail("안녕하세요. 판단에 오신 것을 환영합니다 !")
                .build());

        // 유저-프로젝트 테이블에도 저장
        UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project)
                .build();
        userProjectMappingRepository.save(userProjectMapping);
        //노트 생성 (거꾸로 생성해야함, stack형태로 쌓이므로, 가장 위에 올리고 싶은 노트를 가장 마지막에)
        // 프로젝트 상단 헤더 관련

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("해당 프로젝트 상단에서 [칸반] 버튼 옆의 [전체 문서] 버튼을 누르면 프로젝트에 속한 노트들을 게시판 형태로 볼 수 있습니다.")
                .deadline(LocalDate.now().toString())
                .step(Step.PROCESSING.toString())
                .title("\uD83D\uDDC2 [전체 문서] 탭이란?")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("해당 프로젝트 상단에서 [칸반] 버튼을 누르면 프로젝트에 속한 노트들을 칸반 형태로 볼 수 있습니다.")
                .deadline(LocalDate.now().toString())
                .step(Step.PROCESSING.toString())
                .title("\uD83D\uDCDA [칸반] 탭이란?")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("해당 프로젝트 상단 [칸반] 버튼 옆의 [내가 작성한 문서] 버튼을 누르면 해당 프로젝트에 속한, 내가 작성한 노트들을 게시판 형태로 볼 수 있습니다.\n\n" +
                        "좌측 탭의 [내가 작성한 문서]와는 내가 참여한 '모든' 프로젝트에서 내가 작성한 노트를 확인 할 수 있다는 점이 다릅니다.")
                .deadline(LocalDate.now().toString())
                .step(Step.PROCESSING.toString())
                .title("⬆️ [내가 작성한 문서] 탭이란?")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("[내가 작성한 문서] 탭은 내가 작성한 노트들을 게시판 형태로 볼 수 있습니다.\n\n" +
                        "상단 탭의 [내가 작성한 문서]는 '해당' 프로젝트에서 내가 작성한 노트를 모아보고, \n\n" +
                        "좌측 탭의 [내가 작성한 문서]는 '모든' 프로젝트에서 내가 작성한 노트를 모아본다는 점이 다릅니다.")
                .deadline(LocalDate.now().toString())
                .step(Step.PROCESSING.toString())
                .title("⬅️ [내가 작성한 문서] 탭이란?")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("[북마크] 탭은 내가 북마크한 노트들에 대해서 모아보기를 제공합니다. \n\n" +
                        "이 노트 우측 상단의 북마크 버튼을 눌러 \n\n 북마크를 하고 확인해볼까요? \n\n" +
                        "이 모아보기는 내가 참여한 '모든' 프로젝트에서 북마크한 노트를 볼 수 있습니다.")
                .deadline(LocalDate.now().toString())
                .step(Step.PROCESSING.toString())
                .title("✅ [북마크] 탭이란?")
                .build(), currentUser);

        //==============================================================================================================


        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("프로젝트에 삭제하고 싶으신가요? ㅠㅠ \n\n" +
                        "상단 [톱니바퀴] 버튼으로 프로젝트를 삭제할 수 있어요. \n\n" +
                        "해당 프로젝트를 생성한 사람만 프로젝트 삭제가 가능하며,  \n\n" +
                        "해당 프로젝트에 참여한 사람은 프로젝트 탈퇴만 가능합니다 !")
                .deadline(LocalDate.now().toString())
                .step(Step.TODO.toString())
                .title("❌ 프로젝트 삭제/탈퇴하기")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("프로젝트에 친구를 초대하고 싶으신가요?, \n\n" +
                        "우측 상단의 [멤버 초대] 버튼으로 초대코드를 발급받아, \n\n" +
                        "친구에게 건네주세요 ! \n\n" +
                        "친구는 좌측 하단의 [프로젝트 초대코드 등록]으로 언제 어디서나 프로젝트에 참여할 수 있습니다 !")
                .deadline(LocalDate.now().toString())
                .step(Step.TODO.toString())
                .title("\uD83E\uDD1D 프로젝트에 친구 초대하기")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("내가 작성한 노트가 기억이 안 나시나요? \n\n" +
                        "우측 상단의 [검색] 탭에서 검색을 해보세요! \n\n" +
                        "1. [전체] 조건은, 내가 참여한 모든 프로젝트에서 노트를 '제목'으로 검색합니다. \n\n" +
                        "2. [북마크 검색] 조건은, 내가 북마크한 노트를 '제목'으로 검색합니다. \n" +
                        "(프로젝트를 탈퇴하면 더 이상 해당 노트는 볼 수 없어요 ㅠㅠ) \n\n" +
                        "3. [내가 작성한 문서 검색] 조건은, 내가 참여한 모든 프로젝트에서, 내가 작성한 문서를 기준으로, '제목'으로 검색합니다 ! \n" +
                        "(마찬가지로 탈퇴를 하게되면 더 이상 해당 노트는 볼 수 없어요)")
                .deadline(LocalDate.now().toString())
                .step(Step.TODO.toString())
                .title("\uD83D\uDD0E 노트 검색하기")
                .build(), currentUser);


        // ============================================================================================================
        //노트 관리하기


        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("작업의 현재 상태를 바꿔야 할 필요가 생긴 노트가 생긴다면, \n\n" +
                        "드래그-드롭으로 노트의 위치를 수정해보세요! \n\n" +
                        "노트의 상태 수정이 즉시 반영됩니다! \n\n" +
                        "또한, 같은 상태 내에서도 상하 위치를 바꿀 수 있어요!")
                .deadline(LocalDate.now().toString())
                .step(Step.STORAGE.toString())
                .title("↔️ 칸반에서 문서 이동하기")
                .build(), currentUser);

        Long commentExplainNoteId = noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("우측 상단의 [댓글] 버튼을 눌러 댓글을 확인하고 작성해 보세요! ")
                .deadline(LocalDate.now().toString())
                .step(Step.STORAGE.toString())
                .title("\uD83D\uDD8D 댓글 작성 해보기")
                .build(), currentUser).getNoteId();
        commentService.createComment(commentExplainNoteId, currentUser, CommentCreateRequestDto.builder()
                .content("여긴 댓글이 작성되는 공간이에요 !").build());
        commentService.createComment(commentExplainNoteId, currentUser, CommentCreateRequestDto.builder()
                .content("마음껏 댓글을 달아보세요 !").build());


        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("우측 상단의 [연필] 버튼을 눌러 문서를 수정해 보세요! \n\n" +
                        "수정하러 들어가면 다른 사람은 해당 노트를 수정하러 들어올 수 없어요!")
                .deadline(LocalDate.now().toString())
                .step(Step.STORAGE.toString())
                .title("✏️ 노트 수정 해보기")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("노트를 생성하는 방법은 2가지입니다.\n\n" +
                        "1. 상단 칸반 탭에서 + 버튼을 눌러 노트를 생성합니다 ! \n\n" +
                        "2. 우측 상단 [+ 할 일 만들기]를 눌러 노트를 생성합니다 ! \n\n" +
                        "마감일이 지난 노트는 빨간색으로 표시되니, 마감일이 지난 노트를 생성해 다른점을 확인해보세요 !")
                .deadline(LocalDate.now().toString())
                .step(Step.STORAGE.toString())
                .title("\uD83D\uDCDD 노트 생성 해보기")
                .build(), currentUser);

        // ========================================================================================================
        // 그래서 어떻게 쓰는건데?
        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("같이 여행 할 친구들을 초대해 서로 하고 싶은 여행 계획들을 STORAGE에 올려보세요. \n\n" +
                        "서로의 의견을 모아, TODO를 완성하고, \n\n" +
                        "서로가 생각하는 여행에서 가장 중요한 점들을 정리해서 \n\n" +
                        "빠른 시간안에 원하는 계획을 완성해보세요 !")
                .deadline(LocalDate.now().toString())
                .step(Step.DONE.toString())
                .title("✈️여행 계획도 PandaN에서")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("누구나 편하게 사용할 수 있는 PandaN을 숙제 검사로 이용해 볼까요? \n\n" +
                        "선생님은 숙제를 내주고, 완료하면 노트를 이동시켜 숙제 검사를 체계적으로 관리해 보세요 ! \n\n" +
                        "설명이 필요한 자료는 해당 글에 링크나 파일을 올려 학생에게 알려주세요 ! \n\n" +
                        "학생들은 해당 노트에 댓글로 궁금한 점을 남겨, 선생님께 원격으로 질문 해 보세요 !")
                .deadline(LocalDate.now().toString())
                .step(Step.DONE.toString())
                .title("\uD83D\uDCD6숙제 검사는 PandaN에서")
                .build(), currentUser);

        noteService.createNote(project.getProjectId(), NoteCreateRequestDto.builder()
                .files(new ArrayList<>())
                .content("조별과제 진행 사항을 공유하고,\n\n" +
                        "관련된 파일을 관리하고, \n\n" +
                        "서로에게 도움이 되는 자료나 정보를 댓글을 통해 전달할 수 있습니다 ! \n\n" +
                        "진행 사항을 한 눈에 보며, 부족한 부분을 서로 채워 조별과제를 완벽히 수행해보세요 !")
                .deadline(LocalDate.now().toString())
                .step(Step.DONE.toString())
                .title("\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66조별 과제는 PandaN에서")
                .build(), currentUser);

        return ProjectResponseDto.fromEntity(project);
    }
}
