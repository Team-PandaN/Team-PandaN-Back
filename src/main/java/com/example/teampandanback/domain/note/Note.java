package com.example.teampandanback.domain.note;

import com.example.teampandanback.domain.Timestamped;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor
public class Note extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTE_ID")
    private Long noteId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "DEADLINE")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "STEP")
    private Step step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;


    @Builder
    public Note(String title, String content, LocalDate deadline, Step step, User user, Project project){
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step;
        this.user = user;
        this.project = project;
    }

    // #1
    // What: Note.java에서 changeType 메소드를 삭제하고, update 메소드는 형변환이 완료된 LocalDate 파라미터를 받게 하였습니다.
    // Why: NoteService.java 에서 형변환이 자주 일어나는 바, NoteService.java 에서 형변환 메소드를 정적으로 정의하여 공용으로 쓰기 위함입니다.
    // How: NoteService의 updateNoteDetail 함수는 전달받은 noteRequestDto의 String을 꺼내 localDate으로 변환 후 여기에 전달합니다.
    public void update(NoteUpdateRequestDto noteUpdateRequestDto, LocalDate updateLocalDate, Step step) {
        this.title = noteUpdateRequestDto.getTitle();
        this.content = noteUpdateRequestDto.getContent();
        this.deadline = updateLocalDate;
        this.step = step;
    }

    // #2
    // What: of 메소드를 만들어서 dto를
    // Why: 서비스에서
    // How:
    public static Note of(NoteCreateRequestDto noteCreateRequestDto, LocalDate deadline, Step step, User user, Project project) {
        return Note.builder()
                .title(noteCreateRequestDto.getTitle())
                .content(noteCreateRequestDto.getContent())
                .deadline(deadline)
                .step(step)
                .user(user)
                .project(project)
                .build();
    }
}
