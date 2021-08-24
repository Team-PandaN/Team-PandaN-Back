package com.example.teampandanback.controller;

import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteMoveRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.service.NoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"노트"})
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NoteController {

    private final NoteService noteService;

    //노트 칸반형 조회
    @ApiOperation(value = "노트 칸반형 조회", notes = "프로젝트 참여자만 조회 가능")
    @GetMapping("/projects/{projectId}/kanbans")
    public KanbanNoteSearchResponseDto kanbanNoteSearchResponse(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.readKanbanNote(projectId, userDetails.getUser());
    }

    //내가 쓴 노트 조회
    @ApiOperation(value = "특정 프로젝트에서 내가 쓴 노트 조회", notes = "i.e) ?page=1&size=3")
    @GetMapping("/projects/{projectId}/mynotes")
    public NoteMineInProjectResponseDto readNotesMineOnly(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("page") int page, @RequestParam("size") int size) {
        return noteService.readNotesMineOnly(projectId, userDetails.getUser(), page, size);
    }

    //내가 북마크한 노트 조회
    @ApiOperation(value = "전체 프로젝트에서 내가 북마크 한 노트 조회")
    @GetMapping("/notes/mybookmarks")
    public NoteBookmarkedResponseDto readBookmarkedMine(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("page") int page, @RequestParam("size") int size) {
        return noteService.readBookmarkedMine(userDetails.getUser(), page, size);
    }

    //노트 상세 조회
    @ApiOperation(value = "노트 상세 조회", notes = "해당 노트가 있는 프로젝트의 참여자만 조회 가능")
    @GetMapping("/notes/{noteId}")
    public NoteDetailResponseDto noteDetail(@PathVariable("noteId") Long noteId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.readNoteDetail(noteId, userDetails.getUser());
    }

    //내가 생성
    @ApiOperation(value = "노트 생성", notes = "프로젝트 참여자만 생성 가능")
    @PostMapping("/notes/{projectId}")
    public NoteCreateResponseDto createNote(@PathVariable Long projectId,
                                            @RequestBody NoteCreateRequestDto noteCreateRequestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.createNote(projectId, noteCreateRequestDto, userDetails.getUser());
    }

    //노트 상세 조회에서 수정
    @ApiOperation(value = "노트 상세 조회에서 수정", notes = "해당 노트가 있는 프로젝트의 참여자라면 모두 수정 가능")
    @PutMapping("/notes/details/{noteId}")
    public NoteUpdateResponseDto updateNote(@PathVariable("noteId") Long noteId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody NoteUpdateRequestDto noteUpdateRequestDto) {
        return noteService.updateNoteDetail(noteId, userDetails.getUser(), noteUpdateRequestDto);
        //  서비스의 메소드명은 변경될수있습니다.
    }

    //칸반형 조회 화면에서 노트 이동하여 수정
    @ApiOperation(value = "칸반형 조회 화면에서 노트 이동하여 수정", notes = "해당 노트가 있는 프로젝트의 참여자라면 모두 이동 가능")
    @PutMapping("/notes/{noteId}")
    public NoteUpdateResponseDto moveNote(@PathVariable("noteId") Long noteId, @RequestBody NoteMoveRequestDto noteMoveRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.moveNote(noteId, noteMoveRequestDto, userDetails.getUser());
        //  서비스의 메소드명은 변경될수있습니다.
    }

    //노트 삭제
    @ApiOperation(value = "노트 삭제", notes = "해당 노트가 있는 프로젝트의 참여자라면 모두 삭제 가능")
    @DeleteMapping("/notes/{noteId}")
    public NoteDeleteResponseDto deleteNote(@PathVariable("noteId") Long noteId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.deleteNote(noteId, userDetails.getUser());
    }

    //노트 일반형 조회
    @ApiOperation(value = "노트 일반형 조회", notes = "해당 노트가 있는 프로젝트의 참여자만 조회 가능")
    @GetMapping("/projects/{projectId}/issues")
    public NoteSearchResponseDto ordinaryNoteSearch(@PathVariable("projectId") Long projectId, @RequestParam("page") int page,
                                                    @RequestParam("size") int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return noteService.readOrdinaryNote(projectId, page, size, userDetails.getUser());
    }

    // 전체 프로젝트에서 내가 작성한 노트 조회
    @ApiOperation(value = "전체 프로젝트에서 내가 작성한 노트 조회", notes = "유저가 썼더라도 지금은 프로젝트에 참여하지 않고 있다면 조회 불가능")
    @GetMapping("/notes/mynotes")
    public NoteMineInTotalResponseDto readMyNoteInTotalProject(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("page") int page, @RequestParam("size") int size) {
        return noteService.readMyNoteInTotalProject(userDetails.getUser(), page, size);
    }

    // 사용자가 멤버인 프로젝트들 중에서 노트 제목 검색
    @ApiOperation(value = "내가 참여한 프로젝트들 중에서 노트 검색 (제목으로)")
    @GetMapping("/notes/search")
    public NoteSearchInTotalResponseDto searchNoteInMyProjects(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("keyword") String rawKeyword) {
        return noteService.searchNoteInMyProjects(userDetails.getUser(), rawKeyword);
    }

    // 내가 쓴 문서들 중에서 노트 제목 검색
    @ApiOperation(value = "내가 쓴 노트들 중에서 노트 검색 (제목으로)")
    @GetMapping("/notes/search/mynotes")
    public NoteSearchInMineResponseDto searchNoteInMyNotes(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("keyword") String rawKeyword) {
        return noteService.searchNoteInMyNotes(userDetails.getUser(), rawKeyword);
    }

    @ApiOperation(value = "현재 수정하려 하는 노트가 잠겨있는가")
    @GetMapping("/notes/is-lock/{noteId}")
    public isLockResponseDTO isLock(@PathVariable Long noteId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return noteService.isLock(noteId, userDetails.getUser());
    }

    @ApiOperation(value = "현재 노트를 수정중임을 주기적으로 알림")
    @PostMapping("/notes/writing/{noteId}")
    public void writing(@PathVariable Long noteId){
        System.out.println("===im-writing 요청 ===");
        noteService.writing(noteId);
    }

    @ApiOperation(value = "해당 문서의 잠금 매니저를 시작함")
    @PostMapping("/notes/start-lock-manager/{noteId}")
    public void initLockManager(@PathVariable Long noteId) throws InterruptedException {
        noteService.initLockManager(noteId);
    }
}
