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

    @Column(name = "PREVIOUS")
    private Long previous;

    @Column(name = "NEXT")
    private Long next;

    @Builder
    public Note(String title, String content, LocalDate deadline, Step step, User user, Project project, Long previous, Long next){
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step;
        this.user = user;
        this.project = project;
        this.previous = previous;
        this.next = next;
    }

    public void update(NoteUpdateRequestDto noteUpdateRequestDto, LocalDate updateLocalDate, Step step, Long previous, Long next) {
        this.title = noteUpdateRequestDto.getTitle();
        this.content = noteUpdateRequestDto.getContent();
        this.deadline = updateLocalDate;
        this.step = step;
        this.previous = previous;
        this.next = next;
    }

    public void updateWhileCreate(Long next) {
        this.next = next;
    }

    public static Note of(NoteCreateRequestDto noteCreateRequestDto, LocalDate deadline, Step step, User user, Project project, Long previous, Long next) {
        return Note.builder()
                .title(noteCreateRequestDto.getTitle())
                .content(noteCreateRequestDto.getContent())
                .deadline(deadline)
                .step(step)
                .user(user)
                .project(project)
                .previous(previous)
                .next(next)
                .build();
    }
}
