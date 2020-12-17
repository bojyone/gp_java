package main.controller;

import main.model.DTO.AllPostsDTO;
import main.model.DTO.PostDetailDTO;
import main.model.DTO.PostResponseUser;
import main.services.AuthService;
import main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


    @GetMapping("/api/post/")
    public ResponseEntity postList(@RequestParam(defaultValue = "0") Integer offset,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam(required = false, defaultValue = "recent") String mode)
    {

        AllPostsDTO posts = postService.getAllPosts(mode, limit, offset);
        return new ResponseEntity(posts, HttpStatus.OK);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity postDetail(@RequestHeader("token") String token,
                                     @PathVariable int id)
    {
        PostDetailDTO post = postService.getPostDetail(id);
        if(post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        postService.postViewCountIncrement(null, post);


        return new ResponseEntity(post, HttpStatus.OK);

    }


    @GetMapping("/api/post/search/")
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
    public ResponseEntity myPosts(@RequestHeader("token") String token,
                                  @RequestParam(defaultValue = "0") Integer offset,
                                  @RequestParam(defaultValue = "10") Integer limit,
                                  @RequestParam(defaultValue = "published") String status) {

        if (authService.tokenCheck(token)) {
            AllPostsDTO userPosts = postService.getUserAllPosts(status, limit, offset, authService.userAuthorizationCheck(token).getUser().getId());
            return new ResponseEntity(userPosts, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);

    }
}
