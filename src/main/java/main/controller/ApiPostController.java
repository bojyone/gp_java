package main.controller;

import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/api/post/")
    public Map<String, Object> list()
    {
        Iterable<Post> postIterable = postRepository.findAll();

        Map<String, Object> general = new HashMap<>();
        ArrayList<Object> posts = new ArrayList<>();
        HashMap<String, Object> post = new HashMap<>();
        HashMap<String, Object> user = new HashMap<>();
        List<PostVote> postVotes;
        int likeCount;
        int dislikeCount;

        for(Post p : postIterable) {

            post.put("id", p.getId());
            post.put("time", p.getTime());
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
            general.put("count", posts.size());
            general.put("posts", posts);

            post = new HashMap<>();
            user = new HashMap<>();
        }

        return general;
    }
}
