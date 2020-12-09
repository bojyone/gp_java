package main.controller;

import main.model.DTO.AllPostsDTO;
import main.model.DTO.PostDTO;
import main.model.DTO.PostDetailDTO;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import main.services.PostService;
import main.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ApiPostController {

    @Autowired
    private PostService postService;


    @GetMapping("/api/post/")
    public ResponseEntity postList(@RequestParam(defaultValue = "0") Integer offset,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam(required = false, defaultValue = "recent") String mode)
    {

        AllPostsDTO posts = postService.getAllPosts(mode, limit, offset);
        return new ResponseEntity(posts, HttpStatus.OK);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getDetail(@PathVariable int id)
    {
        PostDetailDTO post = postService.getPostDetail(id);
        if(post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

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


    @Autowired
    private TagService tagService;

    @GetMapping("/test/")
    public ResponseEntity test() {
        tagService.test();
        return new ResponseEntity(HttpStatus.OK);
    }
}
