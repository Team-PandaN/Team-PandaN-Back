package com.example.teampandanback.testData;

import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.Role;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.LocalDate;

//@Component
public class testDataRunner implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProjectMappingRepository userProjectMappingRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user1 = User.builder()
                .email("Panda@naver.com")
                .name("판다")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .email("Tiger@google.com")
                .name("호랑이")
                .role(Role.USER)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .email("Chicken@kakao.com")
                .name("닭")
                .role(Role.USER)
                .build();
        userRepository.save(user3);

        User user4 = User.builder()
                .email("rat@slack.com")
                .name("쥐")
                .role(Role.USER)
                .build();
        userRepository.save(user4);

        User user5 = User.builder()
                .email("cow@kakao.com")
                .name("소")
                .role(Role.USER)
                .build();
        userRepository.save(user5);

        User user6 = User.builder()
                .email("dragon@kakao.com")
                .name("용")
                .role(Role.USER)
                .build();
        userRepository.save(user6);

        User user7 = User.builder()
                .email("horse@kakao.com")
                .name("말")
                .role(Role.USER)
                .build();
        userRepository.save(user7);

        User user8 = User.builder()
                .email("rhino@kakao.com")
                .name("코뿔소")
                .role(Role.USER)
                .build();
        userRepository.save(user8);
        //===========================================
        Project project1 = Project.builder()
                .title("Project1")
                .detail("Project1->detail")
                .build();
        projectRepository.save(project1);

        Project project2 = Project.builder()
                .title("Project2")
                .detail("Project2->detail")
                .build();
        projectRepository.save(project2);

        Project project3 = Project.builder()
                .title("Project3")
                .detail("Project3->detail")
                .build();
        projectRepository.save(project3);
        //===========================================
        UserProjectMapping userProjectMapping1 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .project(project1)
                .user(user1)
                .build();
        userProjectMappingRepository.save(userProjectMapping1);

        UserProjectMapping userProjectMapping2 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .project(project2)
                .user(user2)
                .build();
        userProjectMappingRepository.save(userProjectMapping2);

        UserProjectMapping userProjectMapping3 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .project(project3)
                .user(user3)
                .build();
        userProjectMappingRepository.save(userProjectMapping3);
        //==============================================
        UserProjectMapping userProjectMapping4 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.CREW)
                .project(project1)
                .user(user2)
                .build();
        userProjectMappingRepository.save(userProjectMapping4);

        UserProjectMapping userProjectMapping5 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.CREW)
                .project(project1)
                .user(user3)
                .build();
        userProjectMappingRepository.save(userProjectMapping5);

        //=================================================
        UserProjectMapping userProjectMapping6 = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.CREW)
                .project(project2)
                .user(user3)
                .build();
        userProjectMappingRepository.save(userProjectMapping6);
        //==================================================
        Note note1 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:1 , Project:1 에 작성한 노트1")
                .user(user1)
                .project(project1)
                .deadline(LocalDate.of(2019,11,12))
                .content("컨텐츠1")
                .build();
        noteRepository.save(note1);

        Note note2 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:1 , Project:1 에 작성한 노트2")
                .user(user1)
                .project(project1)
                .deadline(LocalDate.of(2019,11,13))
                .content("컨텐츠2")
                .build();
        noteRepository.save(note2);

        Note note3 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:1 , Project:1 에 작성한 노트3")
                .user(user1)
                .project(project1)
                .deadline(LocalDate.of(2019,11,14))
                .content("컨텐츠3")
                .build();
        noteRepository.save(note3);

        Note note4 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:2 , Project:2 에 작성한 노트1")
                .user(user2)
                .project(project2)
                .deadline(LocalDate.of(2019,11,14))
                .content("컨텐츠4")
                .build();
        noteRepository.save(note4);

        Note note5 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:2 , Project:2 에 작성한 노트3")
                .user(user2)
                .project(project2)
                .deadline(LocalDate.of(2019,11,14))
                .content("컨텐츠5")
                .build();
        noteRepository.save(note5);

        Note note6 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:2 , Project:2 에 작성한 노트3")
                .user(user2)
                .project(project2)
                .deadline(LocalDate.of(2019,11,14))
                .content("컨텐츠3")
                .build();
        noteRepository.save(note6);

        Note note7 = Note.builder()
                .step(Step.PROCESSING)
                .title("User:3 , Project:2 에 작성한 노트3")
                .user(user3)
                .project(project2)
                .deadline(LocalDate.of(2019,11,14))
                .content("컨텐츠4")
                .build();
        noteRepository.save(note7);

        //===========================================================

        Bookmark bookmark1 = Bookmark.builder()
                .user(user2)
                .note(note7)
                .build();
        bookmarkRepository.save(bookmark1);






    }
}
