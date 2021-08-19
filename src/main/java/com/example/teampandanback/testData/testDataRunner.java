package com.example.teampandanback.testData;

import com.example.teampandanback.domain.Comment.Comment;
import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Switching
@Component
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

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<User> userList = new ArrayList<>();
        List<Project> projectList = new ArrayList<>();
        List<UserProjectMapping> userProjectMappingList = new ArrayList<>();
        List<Note> noteList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();
        List<Bookmark> bookmarkList = new ArrayList<>();

        int UserNumber = 10;
        for (int i = 1; i <= UserNumber; i++) {
            String strNum = Integer.toString(i);
            User user = User.builder()
                    .name("User" + strNum)
                    .password(passwordEncoder.encode("User" + strNum))
                    .email("User" + strNum + "@")
                    .picture("https://s3.ap-northeast-2.amazonaws.com/front.blossomwhale.shop/ico-user.svg")
                    .build();
            userRepository.save(user);
            userList.add(user);

            Project project = Project.builder()
                    .title("Project" + strNum)
                    .detail("Project" + strNum + "OWNER:USER" + strNum)
                    .build();
            projectRepository.save(project);
            projectList.add(project);

            UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                    .userProjectRole(UserProjectRole.OWNER)
                    .user(user)
                    .project(project)
                    .build();
            userProjectMappingRepository.save(userProjectMapping);
            userProjectMappingList.add(userProjectMapping);
        }

        for (int i = 1; i <= 80; i++) {
            int userRand = (int) (Math.random() * UserNumber);
            int projectRand = (int) (Math.random() * UserNumber);

            UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                    .userProjectRole(UserProjectRole.CREW)
                    .user(userList.get(userRand))
                    .project(projectList.get(projectRand))
                    .build();

            boolean duplicate = false;
            for (UserProjectMapping each : userProjectMappingList) {
                if (each.getUser().equals(userProjectMapping.getUser()) &&
                        each.getProject().equals(userProjectMapping.getProject())) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate == false) {
                userProjectMappingRepository.save(userProjectMapping);
                userProjectMappingList.add(userProjectMapping);
            }
        }

        // 노트 생성
        for (int i = 0; i < 700; i++) {
            int userProjectRand = (int) (Math.random() * userProjectMappingList.size());
            UserProjectMapping userProjectMapping = userProjectMappingList.get(userProjectRand);
            int enumRand = (int) (Math.random() * 4 + 1);
            Step step;
            if (enumRand == 1) {
                step = Step.STORAGE;
            } else if (enumRand == 2) {
                step = Step.TODO;
            } else if (enumRand == 3) {
                step = Step.PROCESSING;
            } else {
                step = Step.DONE;
            }

            Note note = Note.builder()
                    .user(userProjectMapping.getUser())
                    .project(userProjectMapping.getProject())
                    .deadline(LocalDate.parse("2015-10-" + (int) (Math.random() * 21 + 10)))
                    .title("title"+(int)(Math.random()*100))
                    .content("content")
                    .step(step)
                    .build();
            noteRepository.save(note);
            noteList.add(note);
        }

        //북마크 생성
        for(int i =1; i<= 500; i++) {
            int noteRand = (int) (Math.random() * noteList.size());
            Note note = noteList.get(noteRand);

            Bookmark bookmark = Bookmark.builder()
                    .user(note.getUser())
                    .note(note)
                    .build();

            boolean duplicate = false;
            for (Bookmark each : bookmarkList) {
                if (each.getUser().equals(bookmark.getUser()) &&
                        each.getNote().equals(bookmark.getNote())) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate == false) {
                bookmarkRepository.save(bookmark);
                bookmarkList.add(bookmark);
            }
        }

        //코멘트 생성
        for(int i = 1 ; i<=500;i++){
            int userRand = (int)(Math.random()*userList.size());
            int noteRand = (int)(Math.random()*noteList.size());

            User user = userList.get(userRand);
            Note note = noteList.get(noteRand);

            Comment comment = Comment.builder()
                    .note(note)
                    .user(user)
                    .content("User:"+user.getUserId()+"이 Note:"+note.getNoteId()+"에 작성")
                    .build();

            commentRepository.save(comment);
            commentList.add(comment);
        }

        System.out.println("DONE");


//        User user1 = User.builder()
//                .name("User1")
//                .password(passwordEncoder.encode("User1"))
//                .email("User1@")
//                .build();
//        userRepository.save(user1);
        //===========================================
//        Project project1 = Project.builder()
//                .title("Project1")
//                .detail("Project1->detail")
//                .build();
//        projectRepository.save(project1);
//
//        Project project2 = Project.builder()
//                .title("Project2")
//                .detail("Project2->detail")
//                .build();
//        projectRepository.save(project2);
//
//        Project project3 = Project.builder()
//                .title("Project3")
//                .detail("Project3->detail")
//                .build();
//        projectRepository.save(project3);
//        //===========================================
//        UserProjectMapping userProjectMapping1 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.OWNER)
//                .project(project1)
//                .user(user1)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping1);
//
//        UserProjectMapping userProjectMapping2 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.OWNER)
//                .project(project2)
//                .user(user2)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping2);
//
//        UserProjectMapping userProjectMapping3 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.OWNER)
//                .project(project3)
//                .user(user3)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping3);
//        //==============================================
//        UserProjectMapping userProjectMapping4 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.CREW)
//                .project(project1)
//                .user(user2)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping4);
//
//        UserProjectMapping userProjectMapping5 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.CREW)
//                .project(project1)
//                .user(user3)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping5);
//
//        //=================================================
//        UserProjectMapping userProjectMapping6 = UserProjectMapping.builder()
//                .userProjectRole(UserProjectRole.CREW)
//                .project(project2)
//                .user(user3)
//                .build();
//        userProjectMappingRepository.save(userProjectMapping6);
//        //==================================================
//        Note note1 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:1 , Project:1 에 작성한 노트1")
//                .user(user1)
//                .project(project1)
//                .deadline(LocalDate.of(2019, 11, 12))
//                .content("컨텐츠1")
//                .build();
//        noteRepository.save(note1);
//
//        Note note2 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:1 , Project:1 에 작성한 노트2")
//                .user(user1)
//                .project(project1)
//                .deadline(LocalDate.of(2019, 11, 13))
//                .content("컨텐츠2")
//                .build();
//        noteRepository.save(note2);
//
//        Note note3 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:1 , Project:1 에 작성한 노트3")
//                .user(user1)
//                .project(project1)
//                .deadline(LocalDate.of(2019, 11, 14))
//                .content("컨텐츠3")
//                .build();
//        noteRepository.save(note3);
//
//        Note note4 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:2 , Project:2 에 작성한 노트1")
//                .user(user2)
//                .project(project2)
//                .deadline(LocalDate.of(2019, 11, 14))
//                .content("컨텐츠4")
//                .build();
//        noteRepository.save(note4);
//
//        Note note5 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:2 , Project:2 에 작성한 노트3")
//                .user(user2)
//                .project(project2)
//                .deadline(LocalDate.of(2019, 11, 14))
//                .content("컨텐츠5")
//                .build();
//        noteRepository.save(note5);
//
//        Note note6 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:2 , Project:2 에 작성한 노트3")
//                .user(user2)
//                .project(project2)
//                .deadline(LocalDate.of(2019, 11, 14))
//                .content("컨텐츠3")
//                .build();
//        noteRepository.save(note6);
//
//        Note note7 = Note.builder()
//                .step(Step.PROCESSING)
//                .title("User:3 , Project:2 에 작성한 노트3")
//                .user(user3)
//                .project(project2)
//                .deadline(LocalDate.of(2019, 11, 14))
//                .content("컨텐츠4")
//                .build();
//        noteRepository.save(note7);
//
//        //===========================================================
//
//        Bookmark bookmark1 = Bookmark.builder()
//                .user(user2)
//                .note(note7)
//                .build();
//        bookmarkRepository.save(bookmark1);


    }
}
