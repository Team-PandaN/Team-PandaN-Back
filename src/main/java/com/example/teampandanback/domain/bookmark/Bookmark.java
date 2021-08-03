package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@DynamicUpdate
@Entity
public class Bookmark {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "SEQ")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "NOTE_ID")
    private Note note;

    @Builder
    public Bookmark(User user, Note note) {
        this.user = user;
        this.note = note;
    }
}
