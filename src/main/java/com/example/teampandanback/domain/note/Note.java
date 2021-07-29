package com.example.teampandanback.domain.note;

import com.example.teampandanback.dto.note.NoteRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "NOTE_TB")
@DynamicUpdate
@NoArgsConstructor
public class Note {
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


    public LocalDate changeType (String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;

    }
    public void update(NoteRequestDto noteRequestDto) {
        this.title = noteRequestDto.getTitle();
        this.content = noteRequestDto.getContent();
        this.deadline = changeType(noteRequestDto.getDeadline());
    }
}
