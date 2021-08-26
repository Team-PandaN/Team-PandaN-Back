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
        int projectCount = (int) (userCount * 1.25); // projectCount는 userCount * userInvitedToProjectCountUpperBound < projectCount여야 한다.

        int userInvitedToProjectCountLowerBound = 5;
        int userInvitedToProjectCountUpperBound = 10; // 유저는 프로젝트 N개에만 참여 가능하다.
        int noteWroteAtProjectCountUpperBound = 100;


        System.out.println("Users are creating...");
        //유저 생성
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
        //유저가 프로젝트를 생성 (OWNER)

        DupRandGenerator userRandGen = DupRandGenerator.init(userCount); //
        for (int i = 1; i <= projectCount; i++) {

            if (userRandGen.isEmpty()) { // 더 이상 user가 프로젝트에 참여할 수 없음. 각 유저가 모두 프로젝트를 10개 초과 만들었음.
                break;
            } // 이런 일은, U * 10 < P 일 때, 발생함.

            int userRand = userRandGen.get();


            //지금 이 유저가 프로젝트를 상계만큼 생성 했는가? 그렇다면 이 유저는 안됌. 그리고 다시 뽑음
            while (true) {
                if (userService.getCountOfUserInvitedToProject((long) userRand) >= userInvitedToProjectCountUpperBound) {
                    userRandGen.dropNumber(userRand);

                    if (userRandGen.isEmpty()) {
                        break;
                    } // 이런 일은, U * 10 < P 일 때, 발생함.
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
                    () -> new IllegalArgumentException("프로젝트 생성 중 문제 발생")
            );

            UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                    .userProjectRole(UserProjectRole.OWNER)
                    .user(randUser)
                    .project(project)
                    .build();
            userProjectMappingRepository.save(userProjectMapping);
        }

        System.out.println("Users are inviting to Projects...");
        //유저들이 다른 유저의 프로젝트에 참여 시도 (CREW)

        //모든 유저들이 하계보다는 많은 프로젝트에 참여하도록 한다.
        //따라서 유저를 기준으로 루프를 돈다.

        for (int i = 1; i <= userCount; i++) {
            String userNumStr = Integer.toString(i);

            NoDupRandGenerator projectRandGen = NoDupRandGenerator.init(projectCount);
            while (userService.getCountOfUserInvitedToProject((long) i) < userInvitedToProjectCountLowerBound && !projectRandGen.isEmpty()) {
                int projectRand = projectRandGen.get();

                if (!userService.isUserInvitedToProject((long) i, (long) projectRand)) { //참여 안했으면 걍 CREW로 참가
                    User user = userRepository.findById((long) i).orElseThrow(
                            () -> new IllegalArgumentException("유저 CREW 참여시도에서 유저가 없는 문제 발생")
                    );

                    Project project = projectRepository.findById((long) projectRand).orElseThrow(
                            () -> new IllegalArgumentException("유저 CREW 참여시도에서 프로젝트가 없는 문제 발생")
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
        // 노트 생성

        //도저히 균일하게 노트를 생성할 방안이 떠오르지 않아, 확률에 기댈 예정.
        //프로젝트를 기준으로 확률에 의존함.
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
                            .fileName("나 파일")
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
                        () -> new IllegalArgumentException("노트 생성시 프로젝트 매핑에서 문제 발생")
                );

                noteService.createNote((long) i, noteCreateRequestDto, participatedUsers.get(userRand - 1));
            }
        }

        System.out.println("Bookmarks are creating...");
        //북마크 생성
        for (int i = 1; i <= projectCount; i++) {

            List<User> participatedUsers = userProjectMappingRepository.getUsersByProjectId((long) i);
            List<Note> notesInProject = noteRepository.findAllByProjectId((long) i);

            NoDupRandGenerator notesInProjectRandGen = NoDupRandGenerator.init(notesInProject.size());

            while (!notesInProjectRandGen.isEmpty()) { // 모든 노트가 타겟이 됨
                int noteRand = notesInProjectRandGen.get(); // target Note (북마크가 쓰일 타겟 노트)
                Note curNote = notesInProject.get(noteRand - 1);

                NoDupRandGenerator participatedUserRandGen = NoDupRandGenerator.init(participatedUsers.size());
                while (!participatedUserRandGen.isEmpty()) { // 모든 유저가 해당 노트에 대해 북마크를 할 지 고민함.
                    int userRand = participatedUserRandGen.get();
                    User curUser = participatedUsers.get(userRand - 1);
                    if ((int) (Math.random() * 10) == 0) {
                        bookmarkService.bookmarkNote(curNote.getNoteId(), curUser);
                    }
                }
            }
        }



        System.out.println("Comments are creating...");
        //코멘트 생성
        for (int i = 1; i <= projectCount; i++) {

            List<User> participatedUsers = userProjectMappingRepository.getUsersByProjectId((long) i);
            List<Note> notesInProject = noteRepository.findAllByProjectId((long) i);

            NoDupRandGenerator notesInProjectRandGen = NoDupRandGenerator.init(notesInProject.size());

            while (!notesInProjectRandGen.isEmpty()) { // 모든 노트가 타겟이 됨
                int noteRand = notesInProjectRandGen.get(); // target Note (북마크가 쓰일 타겟 노트)
                Note curNote = notesInProject.get(noteRand - 1);

                NoDupRandGenerator participatedUserRandGen = NoDupRandGenerator.init(participatedUsers.size());
                while (!participatedUserRandGen.isEmpty()) { // 모든 유저가 해당 노트에 대해 북마크를 할 지 고민함.
                    int userRand = participatedUserRandGen.get();
                    User curUser = participatedUsers.get(userRand - 1);
                    if ((int) (Math.random() * 5) == 0) {
                        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                                .content(curUser.getName()+"가 noteId: "+ curNote.getNoteId() + "에 작성한 코멘트")
                                .build();
                        commentService.createComment(curNote.getNoteId(),curUser,commentCreateRequestDto);
                    }
                }
            }

        }
        System.out.println("DONE");
    }
}

class NoDupRandGenerator { // 연속된 숫자 중, 중복되지 않는 난수를 생성하기 위한 커스텀 클래스
    List<Integer> randList = new ArrayList<>();

    public NoDupRandGenerator(int x) {
        for (int i = 1; i <= x; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList);
    }

    public static NoDupRandGenerator init(int x) { // 0~ (x-1)의 난수를 반환할 준비를 함.
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
