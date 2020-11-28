package main.controller;

import main.comparators.PostMaxComparator;
import main.comparators.PostMinComparator;
import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/api/post/")
    public Map<String, Object> list(@RequestParam(name = "offset", required = false) Integer offset, @RequestParam(name = "limit", required = false) Integer limit,
                                    @RequestParam(name = "mode", required = false) String mode)
    {

        Iterable<Post> postIterable = postRepository.findAll();

        Map<String, Object> general = new HashMap<>();
        ArrayList<HashMap<String, Object>> posts = new ArrayList<>();
        HashMap<String, Object> post = new HashMap<>();
        HashMap<String, Object> user = new HashMap<>();
        List<PostVote> postVotes;
        int likeCount;
        int dislikeCount;

        for(Post p : postIterable) {

            if (p.getIsActive() == 1 || p.getModerationStatus().equals("ACCEPTED")) {
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
            }
        }

        if (mode.equals("recent"))
            Collections.sort(posts, new PostMaxComparator("time"));
        else if (mode.equals("popular"))
            Collections.sort(posts, new PostMaxComparator("commentCount"));
        else if (mode.equals("best"))
            Collections.sort(posts, new PostMaxComparator("likeCount"));
        else if (mode.equals("early"))
            Collections.sort(posts, new PostMinComparator("time"));

        general.put("count", posts.size());

        if (limit != null && offset != null) {
            List<HashMap<String, Object>> newPosts = posts.subList(offset, offset + limit);
            posts = new ArrayList<>(newPosts);
        }
        else if (limit != null) {
            List<HashMap<String, Object>> newPosts = posts.subList(0, limit);
            posts = new ArrayList<>(newPosts);
        }
        else if (offset != null) {
            List<HashMap<String, Object>> newPosts = posts.subList(offset, posts.size());
            posts = new ArrayList<>(newPosts);
        }
        general.put("posts", posts);

        return general;
    }
}
