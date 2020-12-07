package main.controller;

import main.comparators.PostMaxComparator;
import main.comparators.PostMinComparator;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.PostVote;
import main.model.entities.Tag;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
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


    @GetMapping("/api/post/")
    public ResponseEntity postList(@RequestParam Integer offset,
                                   @RequestParam Integer limit,
                                   @DefaultValue("recent")@RequestParam(required = false) String mode)
    {
        List<Post> postIterable = new ArrayList<>();
        List<Post> allActivePost = postRepository.findAllActivePosts();

        int activePostsCount = allActivePost.size();

        if (offset >= activePostsCount)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<Post> slice = allActivePost.subList(offset, activePostsCount);
        if (limit > slice.size())
            limit = slice.size();

        Map<String, Object> general = new HashMap<>();
        ArrayList<HashMap<String, Object>> posts = new ArrayList<>();

        switch (mode) {
            case "recent":
                postIterable = postRepository.findLimitRecentPosts(offset + 1, offset + limit);
                break;
            case "popular":
                postIterable = postRepository.findLimitPopularPosts(offset + 1, offset + limit);;
                break;
            case "best":
                postIterable = postRepository.findLimitBestPosts(offset + 1, offset + limit);
                break;
            case "early":
                postIterable = postRepository.findLimitEarlyPosts(offset + 1, offset + limit);
                break;
        }

        for(Post p : postIterable) {

            posts = addPost(p, posts);
        }

        general.put("count", allActivePost.size());
        general.put("posts", posts);

        return new ResponseEntity(general, HttpStatus.OK);
    }



    private ArrayList<HashMap<String, Object>> addPost(Post p, ArrayList<HashMap<String, Object>> posts) {
        List<PostVote> postVotes;
        int likeCount;
        int dislikeCount;

        post.put("id", p.getId());
        post.put("time", p.getTime().getTime() / 1000);

        user.put("id", p.getUser().getId());
        user.put("name", p.getUser().getName());

        post.put("user", user);
        post.put("title", p.getTitle());

        postVotes = p.getPostVotes();
        likeCount = 0;
        dislikeCount = 0;
        for (PostVote postVote : postVotes)
            if (postVote.getValue() == 1)
                likeCount += 1;
            else
                dislikeCount += 1;

        post.put("likeCount", likeCount);
        post.put("dislikeCount", dislikeCount);
        post.put("commentCount", p.getPostComments().size());

        posts.add(post);

        post = new HashMap<>();
        user = new HashMap<>();
        return posts;
    }
}
