package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.response.NoteSearchInBookmarkResponseDto;
import com.example.teampandanback.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    //북마크 함
    @PostMapping("/api/notes/{noteId}/bookmark")
    public void bookmarkNote(@PathVariable Long noteId, @LoginUser SessionUser sessionUser){
        bookmarkService.bookmarkNote(noteId,sessionUser);
    }

    //북마크 해제
    @PostMapping("/api/notes/{noteId}/unbookmark")
    public void unBookmarkNote(@PathVariable Long noteId, @LoginUser SessionUser sessionUser){
        bookmarkService.unBookmarkNote(noteId,sessionUser);
    }

    // 북마크한 노트들 중에서 노트 제목 검색
    @GetMapping("/api/notes/search/bookmarks")
    public NoteSearchInBookmarkResponseDto searchNoteInBookmark(@LoginUser SessionUser sessionUser, @RequestParam("keyword") String rawKeyword){
        return bookmarkService.searchNoteInBookmarks(sessionUser, rawKeyword);
    }
}
