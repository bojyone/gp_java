package main.controller;

import main.model.DTO.AllPostsDTO;
import main.model.DTO.PostDTO;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Component
@RestController
public class ApiPostController {

    private HashMap<String, Object> post = new HashMap<>();
    private HashMap<String, Object> user = new HashMap<>();
    private PostRepository postRepository;

    @Autowired
    public ApiPostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

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
}
