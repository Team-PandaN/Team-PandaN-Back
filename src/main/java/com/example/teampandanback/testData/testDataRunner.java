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
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.service.BookmarkService;
import com.example.teampandanback.service.CommentService;
import com.example.teampandanback.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    @Override
    public void run(ApplicationArguments args) throws Exception {

        //Initial Value
        int userCount = 100; //
        int projectCount = userCount+25;

        int userInvitedToProjectCountUpperBound = 10; // 유저는 프로젝트 N개에만 참여 가능하다.
        int userInvitedToProjectCountLowerBound = 7;

        int noteWroteAtProjectCountUpperBound = 100;


        //Set Array
        List<User> userList = new ArrayList<>(userCount);
        List<Project> projectList = new ArrayList<>(projectCount);
        List<Integer> userInvitedToProjectCount = new ArrayList<>(userCount); // 각 index의 유저가, 참여한 프로젝트 갯수,
        // 따라서 각 index가 userList와 일치하게끔 설계했으므로, 사이즈는 userCount
        // userInvitedToProjectCountUpperBound 로 제한 예정
        for (int i = 0; i < userCount; i++) {
            userInvitedToProjectCount.add(0);
        }
        //0으로 초기화

        List<Integer> noteWroteAtProjectCount = new ArrayList<>(projectCount); // 각 index의 프로젝트에, 써있는 노트 갯수,
        // 따라서 각 index가 projectList와 일치하게끔 설계했으므로, 사이즈는 projectCount
        // noteWroteAtProjectCountUpperBound로 제한 예정
        for (int i = 0; i < projectCount; i++) {
            noteWroteAtProjectCount.add(0);
        }
        //0으로 초기화화


        List<UserProjectMapping> userProjectMappingList = new ArrayList<>();
        List<Note> noteList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();
        List<Bookmark> bookmarkList = new ArrayList<>();


        System.out.println("Users are creating...");
        //유저 생성
        for (int i = 0; i < userCount; i++) {
            String iterationStr = Integer.toString(i + 1);
            User user = User.builder()
                    .name("User" + iterationStr)
                    .password(passwordEncoder.encode("User" + iterationStr))
                    .email("User" + iterationStr + "@email.com")
                    .picture("https://s3.ap-northeast-2.amazonaws.com/front.blossomwhale.shop/ico-user.svg")
                    .build();
            userRepository.save(user);
            userList.add(user);
        }


        System.out.println("Users are creating projects...");
        //유저가 프로젝트를 생성 (OWNER)

        // 프로젝트 입장에서 볼때, 프로젝트는 중복되지 않은 난수를 생성하며, 아니 걍 순서대로.
        // 프로젝트가 이미 한번 뽑힌, 유저를 다시 선택해도 좋다. (중복된 유저 난수를 사용해도 좋다.) 이와같은 상황은
        // 한 유저가 여러 프로젝트를 생성한 경우이다.

        // 그런데 그 유저는 10개 이상의 프로젝트를 생성해선 안됀다.

        //fix - 위와 같이 코드를 짜면, userRand로 어떤 유저가 이 프로젝트에 뽑힐 지를, 선택할 때,
        //10개라는 프로젝트 제한에 의해, 적합한 유저를 찾기까지 엄청나게 오래 걸릴 확률이 존재.

        //fix - user가 확률상으로 프로젝트를 몇개 가져갈지 결정 할 경우, 최악의 경우 여러 유저들이 프로젝트에 참여하지
        // 못하는 상황이 발생함.

        //fin - 그대로 프로젝트가 유저를 선택하지만, 10개가 찬 유저는 더 이상 랜덤으로 뽑히지 않도록 커스텀 클래스를 설계
        DupRandGenerator userRandGen = DupRandGenerator.init(userCount); //
        for (int i = 0; i < projectCount; i++) {

            if (userRandGen.isEmpty()) { // 더 이상 user가 프로젝트에 참여할 수 없음. 각 유저가 모두 프로젝트를 10개 초과 만들었음.
                break;
            }

            int userRand = userRandGen.get();


            //지금 이 유저가 프로젝트를 상계만큼 생성 했는가? 그렇다면 이 유저는 안됌.
            if (userInvitedToProjectCount.get(userRand) >= userInvitedToProjectCountUpperBound) {
                userRandGen.dropNumber(userRand);

                if (userRandGen.isEmpty()) {
                    break;
                }
                userRand = userRandGen.get();
            }

            String userRandStr = Integer.toString(userRand + 1);
            String projectNumStr = Integer.toString(i + 1);

            Project project = Project.builder()
                    .title("Project" + projectNumStr)
                    .detail("Project-" + projectNumStr + " OWNER:USER" + userRandStr)
                    .build();
            projectRepository.save(project);
            projectList.add(project);

            UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                    .userProjectRole(UserProjectRole.OWNER)
                    .user(userList.get(userRand))
                    .project(project)
                    .build();
            userProjectMappingRepository.save(userProjectMapping);
            userProjectMappingList.add(userProjectMapping);
            userInvitedToProjectCount.set(userRand, userInvitedToProjectCount.get(userRand) + 1);
        }

        System.out.println("Users are inviting to Projects...");
        //유저들이 다른 유저의 프로젝트에 참여 시도 (CREW)

        //모든 유저들이 하계보다는 많은 프로젝트에 참여하도록 한다.
        //따라서 유저를 기준으로 루프를 돈다.

        for (int i = 0; i < userCount; i++) {
            String userNumStr = Integer.toString(i + 1);

            NoDupRandGenerator projectRandGen = NoDupRandGenerator.init(projectCount);
            while (userInvitedToProjectCount.get(i) < userInvitedToProjectCountLowerBound && !projectRandGen.isEmpty()) {
                int projectRand = projectRandGen.get();

                boolean hasAlreadyInvitedToThisProject = false;
                //해당 프로젝트에 이미 참여 했는지,
                for (UserProjectMapping each : userProjectMappingList) {
                    if (each.getUser().getUserId() - 1L == (long) i &&
                            each.getProject().getProjectId() - 1L == (long) projectRand) {
                        hasAlreadyInvitedToThisProject = true;
                        break;
                    }
                }

                if (!hasAlreadyInvitedToThisProject) {
                    //참여 안했으면 걍 CREW로 참가
                    UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                            .user(userList.get(i))
                            .project(projectList.get(projectRand))
                            .userProjectRole(UserProjectRole.CREW)
                            .build();
                    userProjectMappingRepository.save(userProjectMapping);
                    userProjectMappingList.add(userProjectMapping);
                    userInvitedToProjectCount.set(i, userInvitedToProjectCount.get(i) + 1);
                }

            }
        }


        System.out.println("Create Notes...");
        // 노트 생성

        //도저히 균일하게 노트를 생성할 방안이 떠오르지 않아, 확률에 기댈 예정.
        //프로젝트를 기준으로 확률에 의존함.

        int noteIdGenerated = 0;
        for (int i = 0; i < projectCount; i++) {

            for (UserProjectMapping each : userProjectMappingList) {
                if (each.getProject().getProjectId() - 1L == (long) i) {
                    boolean isFull = false;
                    while ((int) (Math.random() * 15) != 0) { // 여깄는 숫자가 기댓값임.
                        // 각각 1/4 확률로 해당 step의 노트를 작성한다.

                        if (noteWroteAtProjectCount.get(i) >= noteWroteAtProjectCountUpperBound) { // 해당 프로젝트에 노트가 꽉 차도 break
                            isFull = true;
                            break;
                        }

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

                        NoteCreateRequestDto noteCreateRequestDto = NoteCreateRequestDto.builder()
                                .content("content")
                                .deadline("2021-08-" + (int) (Math.random() * 21 + 10))
                                .step(step.toString())
                                .title("Author: " + each.getUser().getName() + " " + each.getProject().getTitle())
                                .build();


                        noteService.createNote(each.getProject().getProjectId(), noteCreateRequestDto, each.getUser());
                        noteIdGenerated += 1;
                        Note note = noteRepository.findByIdFetch((long) noteIdGenerated).orElseThrow(
                                () -> new IllegalArgumentException("Something has wrong...")
                        );
                        noteList.add(note);
                        noteWroteAtProjectCount.set(i, noteWroteAtProjectCount.get(i) + 1);
                    }
                    if (isFull == true) {
                        break;
                    }
                }
            }
        }

        System.out.println("Bookmarks are creating...");
        //북마크 생성
        //마찬가지로 확률 이용
        for(int i=0 ;i<noteList.size();i++){
            Long curNoteId = (long)(i+1);
            for(UserProjectMapping each : userProjectMappingList){
                if(each.getProject().getProjectId().equals(noteList.get(i).getProject().getProjectId())){
                    if((int)(Math.random()*3)==0){ // 각 노트에 대해서 이 유저가 북마크 확률 33%
                        bookmarkService.bookmarkNote(curNoteId, each.getUser());
                    }
                }
            }
        }

        System.out.println("Comments are creating...");
        //코멘트 생성
        for(int i=0 ;i<noteList.size();i++){
            Long curNoteId = (long)(i+1);
            for(UserProjectMapping each : userProjectMappingList){
                if(each.getProject().getProjectId().equals(noteList.get(i).getProject().getProjectId())){
                    if((int)(Math.random()*3)==0){ // 각 노트에 대해서 이 유저가 코멘트 작성 확률 33%
                        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                                .content(each.getUser().getName()+"가 noteId: "+ noteList.get(i).getNoteId() + "에 작성한 코멘트")
                                .build();
                        commentService.createComment(curNoteId,each.getUser(),commentCreateRequestDto);
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
        for (int i = 0; i < x; i++) {
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
        for (int i = 0; i < x; i++) {
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
