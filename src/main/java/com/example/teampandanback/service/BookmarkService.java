package com.example.teampandanback.service;

import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.response.NoteEachSearchInBookmarkResponseDto;
import com.example.teampandanback.dto.note.response.NoteSearchInBookmarkResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.PandanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final PandanUtils pandanUtils;

    public void bookmarkNote(Long noteId, SessionUser sessionUser) {

        //북마크 누른 사람
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );
        //북마크 될 노트
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );

        //쿼리
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("연결된 프로젝트가 없습니다.")
        );

        //쿼리
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user와 project mapping을 찾지 못했습니다.")
                );

        //유저가 북마크를 했다는 레코드
        Bookmark bookmark = bookmarkRepository.findByUserAndNote(user, note)
                .orElseGet(() -> Bookmark.builder()
                        .user(user)
                        .note(note)
                        .build());

        bookmarkRepository.save(bookmark);
    }

    public void unBookmarkNote(Long noteId, SessionUser sessionUser) {

        //북마크 누른 사람
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        //북마크 될 노트
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );


        //쿼리
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("연결된 프로젝트가 없습니다.")
        );

        //쿼리
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user와 project mapping을 찾지 못했습니다.")
                );

        // 유저가 북마크를 했다는 레코드드
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndNote(user, note);

        bookmark.ifPresent(bookmarkRepository::delete);

    }

    public NoteSearchInBookmarkResponseDto searchNoteInBookmarks(SessionUser sessionUser, String rawKeyword){
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<NoteEachSearchInBookmarkResponseDto> resultList = bookmarkRepository.findNotesByUserIdAndKeywordInBookmarks(sessionUser.getUserId(), keywordList);
        return NoteSearchInBookmarkResponseDto.builder().noteList(resultList).build();
    }
}
