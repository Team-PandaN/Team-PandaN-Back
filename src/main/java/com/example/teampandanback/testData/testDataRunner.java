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
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Switching
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

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NoteService noteService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    CommentService commentService;

    @PersistenceContext
    EntityManager em;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;


    //    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Initial Value
        int userCount = 3; //
        int projectCount = (int) (userCount * 1.25); // projectCount??? userCount * userInvitedToProjectCountUpperBound < projectCount?????? ??????.

        int userInvitedToProjectCountLowerBound = 5;
        int userInvitedToProjectCountUpperBound = 10; // ????????? ???????????? N????????? ?????? ????????????.
        int noteWroteAtProjectCountUpperBound = 100;


        System.out.println("Users are creating...");
        //?????? ??????
        for (int i = 1; i <= userCount; i++) {
            String iterationStr = Integer.toString(i);
            User user = User.builder()
                    .name("User" + iterationStr)
                    .password(passwordEncoder.encode("User" + iterationStr))
                    .email("User" + iterationStr + "@email.com")
                    .picture("https://s3.ap-northeast-2.amazonaws.com/front.blossomwhale.shop/ico-user.svg")
                    .build();
            userRepository.save(user);
        }

        System.out.println("Users are creating projects...");
        //????????? ??????????????? ?????? (OWNER)

        DupRandGenerator userRandGen = DupRandGenerator.init(userCount); //
        for (int i = 1; i <= projectCount; i++) {

            if (userRandGen.isEmpty()) { // ??? ?????? user??? ??????????????? ????????? ??? ??????. ??? ????????? ?????? ??????????????? 10??? ?????? ????????????.
                break;
            } // ?????? ??????, U * 10 < P ??? ???, ?????????.

            int userRand = userRandGen.get();


            //?????? ??? ????????? ??????????????? ???????????? ?????? ?????????? ???????????? ??? ????????? ??????. ????????? ?????? ??????
            while (true) {
                if (userService.getCountOfUserInvitedToProject((long) userRand) >= userInvitedToProjectCountUpperBound) {
                    userRandGen.dropNumber(userRand);

                    if (userRandGen.isEmpty()) {
                        break;
                    } // ?????? ??????, U * 10 < P ??? ???, ?????????.
                    userRand = userRandGen.get();
                } else {
                    break;
                }
            }


            String userRandStr = Integer.toString(userRand);
            String projectNumStr = Integer.toString(i);

            Project project = Project.builder()
                    .title("Project" + projectNumStr)
                    .detail("Project-" + projectNumStr + " OWNER:USER" + userRandStr)
                    .build();
            projectRepository.save(project);

            User randUser = userRepository.findById((long) userRand).orElseThrow(
                    () -> new IllegalArgumentException("???????????? ?????? ??? ?????? ??????")
            );

            UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                    .userProjectRole(UserProjectRole.OWNER)
                    .user(randUser)
                    .project(project)
                    .build();
            userProjectMappingRepository.save(userProjectMapping);
        }

        System.out.println("Users are inviting to Projects...");
        //???????????? ?????? ????????? ??????????????? ?????? ?????? (CREW)

        //?????? ???????????? ??????????????? ?????? ??????????????? ??????????????? ??????.
        //????????? ????????? ???????????? ????????? ??????.

        for (int i = 1; i <= userCount; i++) {
            String userNumStr = Integer.toString(i);

            NoDupRandGenerator projectRandGen = NoDupRandGenerator.init(projectCount);
            while (userService.getCountOfUserInvitedToProject((long) i) < userInvitedToProjectCountLowerBound && !projectRandGen.isEmpty()) {
                int projectRand = projectRandGen.get();

                if (!userService.isUserInvitedToProject((long) i, (long) projectRand)) { //?????? ???????????? ??? CREW??? ??????
                    User user = userRepository.findById((long) i).orElseThrow(
                            () -> new IllegalArgumentException("?????? CREW ?????????????????? ????????? ?????? ?????? ??????")
                    );

                    Project project = projectRepository.findById((long) projectRand).orElseThrow(
                            () -> new IllegalArgumentException("?????? CREW ?????????????????? ??????????????? ?????? ?????? ??????")
                    );

                    UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                            .user(user)
                            .project(project)
                            .userProjectRole(UserProjectRole.CREW)
                            .build();
                    userProjectMappingRepository.save(userProjectMapping);
                }
            }
        }


        System.out.println("Create Notes...");
        // ?????? ??????

        //????????? ???????????? ????????? ????????? ????????? ???????????? ??????, ????????? ?????? ??????.
        //??????????????? ???????????? ????????? ?????????.
        for (int i = 1; i <= projectCount; i++) {

            List<User> participatedUsers = userProjectMappingRepository.getUsersByProjectId((long) i);
            DupRandGenerator participatedUserRandGen = DupRandGenerator.init(participatedUsers.size());
            while (!participatedUserRandGen.isEmpty() && projectRepository.getCountOfNote((long) i) < 100L) {
                int userRand = participatedUserRandGen.get();

                int enumRand = (int) (Math.random() * 4);
                Step step;
                if (enumRand == 0) {
                    step = Step.STORAGE;
                } else if (enumRand == 1) {
                    step = Step.TODO;
                } else if (enumRand == 2) {
                    step = Step.PROCESSING;
                } else {
                    step = Step.DONE;
                }

                List<FileDetailRequestDto> files = new ArrayList<>();

                for (int j = 0; j < 2; j++) {
                    files.add(FileDetailRequestDto.builder()
                            .fileName("??? ??????")
                            .fileUrl("http://s3.com")
                            .build());
                }

                NoteCreateRequestDto noteCreateRequestDto = NoteCreateRequestDto.builder()
                        .content("content")
                        .deadline("2021-08-" + (int) (Math.random() * 21 + 10))
                        .step(step.toString())
                        .title("Author: " + participatedUsers.get(userRand - 1).getName())
                        .files(files)
                        .build();

                Project project = projectRepository.findById((long) i).orElseThrow(
                        () -> new IllegalArgumentException("?????? ????????? ???????????? ???????????? ?????? ??????")
                );

                noteService.createNote((long) i, noteCreateRequestDto, participatedUsers.get(userRand - 1));
            }
        }

        System.out.println("Bookmarks are creating...");
        //????????? ??????
        for (int i = 1; i <= projectCount; i++) {

            List<User> participatedUsers = userProjectMappingRepository.getUsersByProjectId((long) i);
            List<Note> notesInProject = noteRepository.findAllByProjectId((long) i);

            NoDupRandGenerator notesInProjectRandGen = NoDupRandGenerator.init(notesInProject.size());

            while (!notesInProjectRandGen.isEmpty()) { // ?????? ????????? ????????? ???
                int noteRand = notesInProjectRandGen.get(); // target Note (???????????? ?????? ?????? ??????)
                Note curNote = notesInProject.get(noteRand - 1);

                NoDupRandGenerator participatedUserRandGen = NoDupRandGenerator.init(participatedUsers.size());
                while (!participatedUserRandGen.isEmpty()) { // ?????? ????????? ?????? ????????? ?????? ???????????? ??? ??? ?????????.
                    int userRand = participatedUserRandGen.get();
                    User curUser = participatedUsers.get(userRand - 1);
                    if ((int) (Math.random() * 10) == 0) {
                        bookmarkService.bookmarkNote(curNote.getNoteId(), curUser);
                    }
                }
            }
        }



        System.out.println("Comments are creating...");
        //????????? ??????
        for (int i = 1; i <= projectCount; i++) {

            List<User> participatedUsers = userProjectMappingRepository.getUsersByProjectId((long) i);
            List<Note> notesInProject = noteRepository.findAllByProjectId((long) i);

            NoDupRandGenerator notesInProjectRandGen = NoDupRandGenerator.init(notesInProject.size());

            while (!notesInProjectRandGen.isEmpty()) { // ?????? ????????? ????????? ???
                int noteRand = notesInProjectRandGen.get(); // target Note (???????????? ?????? ?????? ??????)
                Note curNote = notesInProject.get(noteRand - 1);

                NoDupRandGenerator participatedUserRandGen = NoDupRandGenerator.init(participatedUsers.size());
                while (!participatedUserRandGen.isEmpty()) { // ?????? ????????? ?????? ????????? ?????? ???????????? ??? ??? ?????????.
                    int userRand = participatedUserRandGen.get();
                    User curUser = participatedUsers.get(userRand - 1);
                    if ((int) (Math.random() * 5) == 0) {
                        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                                .content(curUser.getName()+"??? noteId: "+ curNote.getNoteId() + "??? ????????? ?????????")
                                .build();
                        commentService.createComment(curNote.getNoteId(),curUser,commentCreateRequestDto);
                    }
                }
            }

        }
        System.out.println("DONE");
    }
}

class NoDupRandGenerator { // ????????? ?????? ???, ???????????? ?????? ????????? ???????????? ?????? ????????? ?????????
    List<Integer> randList = new ArrayList<>();

    public NoDupRandGenerator(int x) {
        for (int i = 1; i <= x; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList);
    }

    public static NoDupRandGenerator init(int x) { // 0~ (x-1)??? ????????? ????????? ????????? ???.
        return new NoDupRandGenerator(x);
    }

    public int get() {
        int result = randList.get(0);
        randList.remove(0);
        return result;
    }

    public boolean isEmpty() {
        return randList.size() == 0;
    }
}

class DupRandGenerator {
    List<Integer> randList = new ArrayList<>();

    public DupRandGenerator(int x) {
        for (int i = 1; i <= x; i++) {
            randList.add(i);
        }
    }

    public static DupRandGenerator init(int x) {
        return new DupRandGenerator(x);
    }

    public int get() {
        int randNum = (int) (Math.random() * randList.size());
        return randList.get(randNum);
    }

    public void dropNumber(int x) {
        randList.remove(x);
    }

    public boolean isEmpty() {
        return randList.size() == 0;
    }

}
