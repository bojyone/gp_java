package main.controller;

import main.model.DTO.*;
import main.model.entities.PostVote;
import main.model.entities.User;
import main.services.AuthService;
import main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class ApiPostController {

    private final PostService postService;
    private final AuthService authService;

    @Autowired
    public ApiPostController(PostService postService, AuthService authService) {
        this.authService = authService;
        this.postService = postService;
    }


    @GetMapping("/api/post")
    public ResponseEntity postList(@RequestParam(defaultValue = "0") Integer offset,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam(required = false, defaultValue = "recent") String mode)
    {

        AllPostsDTO posts = postService.getAllPosts(mode, limit, offset);
        return new ResponseEntity(posts, HttpStatus.OK);
    }


    @GetMapping("/api/post/moderation")
    public ResponseEntity moderation(@RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "10") Integer limit) {

        User moderator = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        AllPostsDTO posts = postService.getModeratorAllPosts(moderator, limit, offset);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/api/post/{id}")
    public ResponseEntity postDetail(@PathVariable int id)
    {
        PostDetailDTO post = postService.getPostDetail(id);
        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        if(post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        postService.postViewCountIncrement(user, post);


        return new ResponseEntity(post, HttpStatus.OK);

    }


    @GetMapping("/api/post/search")
    public ResponseEntity postSearch(@RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "10") Integer limit,
                                     @RequestParam(name = "query", required = false) String query)
    {
        if (query == null)
            return new ResponseEntity(postService.getAllPosts("recent", limit, offset),
                                      HttpStatus.OK);
        AllPostsDTO posts = postService.getSearchResult(offset, limit, query);
        return new ResponseEntity(posts, HttpStatus.OK);
    }


    @GetMapping("/api/post/byDate")
    public ResponseEntity postByDate(@RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "10") Integer limit,
                                     @RequestParam(name = "date") String date)
    {
        AllPostsDTO posts = postService.getSearchResultByDate(offset, limit, date);
        return new ResponseEntity(posts, HttpStatus.OK);
    }


    @GetMapping("/api/post/byTag")
    public ResponseEntity postByTag(@RequestParam(defaultValue = "0") Integer offset,
                                    @RequestParam(defaultValue = "10") Integer limit,
                                    @RequestParam String tag)
    {
        AllPostsDTO posts = postService.getSearchResultByTag(offset, limit, tag);
        return new ResponseEntity(posts, HttpStatus.OK);
    }


    @GetMapping("/api/post/my")
    public ResponseEntity myPosts(@RequestParam(defaultValue = "0") Integer offset,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(defaultValue = "published") String status) {

        if (authService.sessionCheck(RequestContextHolder.currentRequestAttributes().getSessionId())) {
            AllPostsDTO userPosts = postService.getUserAllPosts(status, limit, offset, authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId()).getId());
            return new ResponseEntity(userPosts, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);

    }


    @PostMapping("/api/post/like")
    public ResponseEntity like(@RequestBody PostIdDTO data) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        if (postService.castVote(user, data.getPostId(), (short) 1)) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }
        return new ResponseEntity(new SimpleResponse(false), HttpStatus.OK);
    }


    @PostMapping("/api/post/dislike")
    public ResponseEntity dislike(@RequestBody PostIdDTO data) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        if (postService.castVote(user, data.getPostId(), (short) 0)) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }
        return new ResponseEntity(new SimpleResponse(false), HttpStatus.OK);
    }


    @PostMapping("/api/post")
    public ResponseEntity publication(@RequestBody PostPublishDTO data) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        PostResponseErrors response = postService.publishPost(user, data);

        if (response.getResult()) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity(response, HttpStatus.OK);

    }


    @PutMapping("/api/post/{ID}")
    public ResponseEntity postDetail(@PathVariable int id, PostPublishDTO data) {

        User user = authService.getUserFromSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        PostDetailDTO post = postService.getPostDetail(id);

        if(post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        PostResponseErrors response = postService.editPost(user, data, id);

        if (response.getResult()) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

}
