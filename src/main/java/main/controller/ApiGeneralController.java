package main.controller;

import main.api.response.InitResponse;
import main.model.DTO.*;
import main.model.entities.User;
import main.model.repositories.GlobalSettingRepository;
import main.model.repositories.UserRepository;
import main.services.AuthService;
import main.services.GeneralService;
import main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final GeneralService generalService;
    private final AuthService authService;
    private final PostService postService;


    @Autowired
    public ApiGeneralController(InitResponse initResponse, GeneralService generalService,
                                AuthService authService, PostService postService) {
        this.initResponse = initResponse;
        this.generalService = generalService;
        this.authService = authService;
        this.postService = postService;
    }


    @GetMapping("/api/init")
    private InitResponse init(@RequestBody InitResponse initResponse) {
        return initResponse;
    }


    @GetMapping("/api/calendar")
    public ResponseEntity calendar(@RequestParam(required = false) Integer year) {


        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);

        return new ResponseEntity(generalService.getPostCalendar(year), HttpStatus.OK);
    }


    @GetMapping("/api/tag")
    public ResponseEntity tag(@RequestParam(required = false) String query) {
        return new ResponseEntity(generalService.getTagWeight(query), HttpStatus.OK);
    }


    @GetMapping("/api/settings")
    public ResponseEntity globalSettings() {
        return new ResponseEntity(generalService.getSettings(), HttpStatus.OK);
    }


    @PutMapping("/api/settings")
    public ResponseEntity globalSettings(@RequestBody SettingDTO settings) {
        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());

        if (user == null || user.getIsModerator() == 0) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        generalService.editGlobalSettings(settings);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/api/statistics/all")
    public ResponseEntity allStatistics() {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        StatisticInterface statistics = generalService.getAllStatistics(user);
        if (statistics != null) {
            return new ResponseEntity(statistics, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/api/statistics/my")
    public ResponseEntity userStatistics() {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return new ResponseEntity(generalService.getUserStatistics(user), HttpStatus.OK);
    }


    @PostMapping("/api/comment")
    public ResponseEntity comment(@RequestBody PostSendCommentDTO comment) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());

        if (postService.getParentCommentId(comment) != null &&
            postService.getCommentFromID(postService.getParentCommentId(comment)) == null ||
            postService.getPostDetail(comment.getPostId()) == null) {

            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        PostResponseErrors response = postService.sendComment(user, comment);

        if (!response.getResult()) {

            return new ResponseEntity(response, HttpStatus.OK);
        }

        Integer newCommentId = postService.getParentCommentId(comment);
        Map<String, Integer> successResponse = new HashMap<>();
        successResponse.put("id", newCommentId);

        return new ResponseEntity(successResponse, HttpStatus.OK);
    }


    @PostMapping("/api/profile/my")
    public ResponseEntity profile(@RequestBody EditProfileDTO data) {
        return null;
    }


    //не сказано, по каким причинам не может пройти действие
    @PostMapping("/api/moderation")
    public ResponseEntity moderation(@RequestBody ModeratorAction action) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return new ResponseEntity(generalService.moderatorAct(action, user), HttpStatus.OK);
    }



}
