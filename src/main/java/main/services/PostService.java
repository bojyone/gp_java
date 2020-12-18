package main.services;

import main.model.DTO.*;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
    private final PostRepository postRepository;
    private final AuthService authService;


    @Autowired
    public PostService(PostRepository postRepository, AuthService authService) {

        this.postRepository = postRepository;
        this.authService = authService;
    }

    @Autowired
    private ModelMapper modelMapper;

    public AllPostsDTO getAllPosts(String mode, Integer limit, Integer offset) {

        Pageable paging = PageRequest.of(offset / limit, limit);

        List<PostDTO> posts = (switch (mode) {
            case "popular" -> postRepository.findPopularPosts(paging);
            case "best" -> postRepository.findBestPosts(paging);
            case "early" -> postRepository.findEarlyPosts(paging);
            default -> postRepository.findRecentPosts(paging);
        }).stream().map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allPosts = new AllPostsDTO();
        allPosts.setCount(postRepository.findAllActivePostsCount());
        allPosts.setPost(posts);
        return allPosts;
    }


    public AllPostsDTO getUserAllPosts(String status, Integer limit, Integer offset, Integer userId) {

        Pageable paging = PageRequest.of(offset / limit, limit);

        List<PostDTO> posts = (switch (status) {
            case "inactive" -> postRepository.findUserInactivePosts(userId, paging);
            case "pending" -> postRepository.findUserPendingPosts(userId, paging);
            case "declined" -> postRepository.findUserDeclinedPosts(userId, paging);
            default -> postRepository.findUserPublishedPosts(userId, paging);
        }).stream().map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allUserPosts = new AllPostsDTO();
        allUserPosts.setCount(postRepository.findAllActivePostsCount());
        allUserPosts.setPost(posts);
        return allUserPosts;
    }


    private PostDTO convertToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        List<PostComment> comments = post.getPostComments();
        List<PostVote> votes = post.getPostVotes();
        int likeCount = 0;
        int dislikeCount = 0;

        for (PostVote vote : votes)
            if (vote.getValue() == 1)
                likeCount += 1;
            else
                dislikeCount += 1;

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        UserDTO postAuthor = modelMapper.map(post.getUser(), UserDTO.class);

        postDTO.setId(post.getId());
        postDTO.setTimestamp(post.getTime().getTime() / 1000);
        postDTO.setUser(postAuthor);
        postDTO.setTitle(post.getTitle());
        postDTO.setCommentCount(comments.size());
        postDTO.setLikeCount(likeCount);
        postDTO.setDislikeCount(dislikeCount);
        postDTO.setViewCount(post.getViewCount());

        return postDTO;
    }


    public PostDetailDTO getPostDetail(int id) {
        Post post = postRepository.findDetailPost(id);
        if (post == null)
            return null;

        PostDetailDTO postDetailDTO = new PostDetailDTO();
        List<PostCommentDTO> list = new ArrayList<>();
        List<PostComment> comments = post.getPostComments();
        List<PostVote> votes = post.getPostVotes();
        int likeCount = 0;
        int dislikeCount = 0;

        for (PostVote vote : votes)
            if (vote.getValue() == 1)
                likeCount += 1;
            else
                dislikeCount += 1;

        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.LOOSE);
        UserDTO postAuthor = modelMapper.map(post.getUser(), UserDTO.class);

        for (PostComment comment : comments) {
            modelMapper.getConfiguration()
                       .setMatchingStrategy(MatchingStrategies.LOOSE);
            PostCommentDTO commentDTO = modelMapper.map(comment, PostCommentDTO.class);
            list.add(commentDTO);

        }

        postDetailDTO.setActive(post.getIsActive());
        postDetailDTO.setUser(postAuthor);
        postDetailDTO.setId(post.getId());
        postDetailDTO.setTimestamp(post.getTime().getTime() / 1000);
        postDetailDTO.setTitle(post.getTitle());
        postDetailDTO.setText(post.getText());
        postDetailDTO.setComments(list);
        postDetailDTO.setLikeCount(likeCount);
        postDetailDTO.setDislikeCount(dislikeCount);
        postDetailDTO.setViewCount(post.getViewCount());
        return postDetailDTO;
    }

    public AllPostsDTO getSearchResult(Integer offset, Integer limit, String query) {

        Pageable paging = PageRequest.of(offset / limit, limit);
        List<PostDTO> posts = ((List<Post>) postRepository
                              .findSearchResult(paging, query))
                              .stream()
                              .map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allPosts = new AllPostsDTO();
        allPosts.setCount(posts.size());
        allPosts.setPost(posts);
        return allPosts;
    }

    public AllPostsDTO getSearchResultByDate(Integer offset, Integer limit, String date) {

        Pageable paging = PageRequest.of(offset / limit, limit);
        List<PostDTO> posts = ((List<Post>) postRepository
                .findSearchResultByDate(paging, date))
                .stream()
                .map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allPosts = new AllPostsDTO();
        allPosts.setCount(posts.size());
        allPosts.setPost(posts);
        return allPosts;
    }

    public AllPostsDTO getSearchResultByTag(Integer offset, Integer limit, String tag) {

        Pageable paging = PageRequest.of(offset / limit, limit);
        List<PostDTO> posts = ((List<Post>) postRepository
                .findSearchResultByTag(paging, tag))
                .stream()
                .map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allPosts = new AllPostsDTO();
        allPosts.setCount(posts.size());
        allPosts.setPost(posts);
        return allPosts;
    }


    public void postViewCountIncrement(User user, PostDetailDTO postDTO) {

        if (user == null || user.getId() != postDTO.getUser().getId() || user.getIsModerator() == 0) {

            Post post = postRepository.findDetailPost(postDTO.getId());
            post.setViewCount(post.getViewCount() + 1);
        }
    }
}
