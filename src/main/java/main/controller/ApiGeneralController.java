package main.controller;

import main.model.DTO.*;
import main.model.entities.User;
import main.services.AuthService;
import main.services.GeneralService;
import main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {
    private final InitDTO initResponse = new InitDTO();
    private final GeneralService generalService;
    private final AuthService authService;
    private final PostService postService;


    @Autowired
    public ApiGeneralController(GeneralService generalService,
                                AuthService authService, PostService postService) {
        this.generalService = generalService;
        this.authService = authService;
        this.postService = postService;
    }


    @GetMapping("/api/init")
    private ResponseEntity init() {
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }


    @GetMapping("/api/calendar")
    public ResponseEntity calendar(@RequestParam(required = false) Integer year) {


        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);

        return new ResponseEntity<>(generalService.getPostCalendar(year), HttpStatus.OK);
    }


    @GetMapping("/api/tag")
    public ResponseEntity tag(@RequestParam(required = false) String query) {
        return new ResponseEntity<>(generalService.getTagWeight(query), HttpStatus.OK);
    }


    @GetMapping("/api/settings")
    public ResponseEntity globalSettings() {

        return new ResponseEntity<>(generalService.getSettings(), HttpStatus.OK);
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
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/api/statistics/my")
    public ResponseEntity userStatistics() {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return new ResponseEntity<>(generalService.getUserStatistics(user), HttpStatus.OK);
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

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Integer newCommentId = postService.getParentCommentId(comment);
        Map<String, Integer> successResponse = new HashMap<>();
        successResponse.put("id", newCommentId);

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


    @PostMapping("/api/profile/my")
    public ResponseEntity profile(@RequestBody EditProfileDTO data) {
        return null;
    }


    //не сказано, по каким причинам не может пройти действие
    @PostMapping("/api/moderation")
    public ResponseEntity moderation(@RequestBody ModeratorAction action) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return new ResponseEntity<>(generalService.moderatorAct(action, user), HttpStatus.OK);
    }


    @PostMapping("/api/image")
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {


        if (!file.isEmpty()) {

            String path = generalService.saveImage(file);
            if (path == null) {
                return null;
            }
            return new ResponseEntity<>(path, HttpStatus.OK);
        }
        return null;
    }


}
