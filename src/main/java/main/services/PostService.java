package main.services;

import main.model.DTO.AllPostsDTO;
import main.model.DTO.PostDTO;
import main.model.DTO.UserDTO;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {

        this.postRepository = postRepository;
    }


    public AllPostsDTO getAllPosts(String mode, Integer limit, Integer offset) {

        Pageable paging = PageRequest.of(offset, limit);

        List<PostDTO> posts = new ArrayList<>();
        switch (mode) {
            case "recent":
                posts = ((List<Post>) postRepository
                        .findRecentPosts(paging))
                        .stream()
                        .map(this::convertToPostDTO)
                        .collect(Collectors.toList());
                break;
            case "popular":
                posts = ((List<Post>) postRepository
                        .findPopularPosts(paging))
                        .stream()
                        .map(this::convertToPostDTO)
                        .collect(Collectors.toList());
                break;
            case "best":
                posts = ((List<Post>) postRepository
                        .findBestPosts(paging))
                        .stream()
                        .map(this::convertToPostDTO)
                        .collect(Collectors.toList());
                break;
            case "early":
                posts = ((List<Post>) postRepository
                        .findEarlyPosts(paging))
                        .stream()
                        .map(this::convertToPostDTO)
                        .collect(Collectors.toList());
                break;
        }

        AllPostsDTO allPosts = new AllPostsDTO();
        allPosts.setCount(postRepository.findAllActivePostsCount());
        allPosts.setPost(posts);
        return allPosts;
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        UserDTO userDTO = new UserDTO();
        List<PostComment> comments = post.getPostComments();
        List<PostVote> votes = post.getPostVotes();
        Integer likeCount = 0;
        Integer dislikeCount = 0;

        for (PostVote vote : votes)
            if (vote.getValue() == 1)
                likeCount += 1;
            else
                dislikeCount += 1;

        userDTO.setId(post.getUser().getId());
        userDTO.setName(post.getUser().getName());
        userDTO.setPhoto(post.getUser().getPhoto());

        postDTO.setId(post.getId());
        postDTO.setTimestamp(post.getTime());
        postDTO.setUser(userDTO);
        postDTO.setTitle(post.getTitle());
        postDTO.setCommentCount(comments.size());
        postDTO.setLikeCount(likeCount);
        postDTO.setDislikeCount(dislikeCount);
        postDTO.setViewCount(post.getViewCount());

        return postDTO;
    }


}
