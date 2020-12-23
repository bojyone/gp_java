package main.services;

import main.model.DTO.*;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import main.model.repositories.PostVoteRepository;
import main.model.repositories.Tag2PostRepository;
import main.model.repositories.TagRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tagToPostRepository;
    private final PostVoteRepository postVoteRepository;
    private final ModelMapper modelMapper;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository,
                       ModelMapper modelMapper, TagRepository tagRepository,
                       Tag2PostRepository tagToPostRepository) {

        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.postVoteRepository = postVoteRepository;
        this.modelMapper = modelMapper;

    }

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
        allPosts.setPosts(posts);
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
        allUserPosts.setCount(posts.size());
        allUserPosts.setPosts(posts);
        return allUserPosts;
    }


    public AllPostsDTO getModeratorAllPosts(User moderator, Integer limit, Integer offset) {

        Pageable paging = PageRequest.of(offset / limit, limit);

        List<PostDTO> posts = postRepository.findModeratorPosts(paging, moderator.getId())
                                            .stream().map(this::convertToPostDTO).collect(Collectors.toList());

        AllPostsDTO allModeratorPosts = new AllPostsDTO();
        allModeratorPosts.setCount(posts.size());
        allModeratorPosts.setPosts(posts);

        return allModeratorPosts;
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
        allPosts.setPosts(posts);
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
        allPosts.setPosts(posts);
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
        allPosts.setPosts(posts);
        return allPosts;
    }


    public void postViewCountIncrement(User user, PostDetailDTO postDTO) {

        if (user == null || user.getId() != postDTO.getUser().getId() || user.getIsModerator() == 0) {

            Post post = postRepository.findDetailPost(postDTO.getId());
            post.setViewCount(post.getViewCount() + 1);
        }
    }


    public boolean castVote(User user, Integer postId, Short value) {

        PostVote userPostVote = postVoteRepository.findUserPostVote(postId, user.getId());
        if (userPostVote == null) {

            postVoteRepository.savePostVote(user.getId(), postId, formatter.format(new Date()), value);

            return  true;
        }

        if (!userPostVote.getValue().equals(value)) {

            userPostVote.setValue(value);
            userPostVote.setTime(new Date());

            return true;
        }

        return false;
    }


    public PostResponseErrors publishPost(User user, PostPublishDTO data) {
        PostResponseErrors response = checkPostValidity(data);

        if (response != null && !response.getResult()) {
            return response;
        }

        if (data.getTimestamp() <= System.currentTimeMillis()) {
            postRepository.savePost(data.getActive(), data.getText(), formatter.format(new Date()),
                                    data.getTitle(), user.getId());

        }

        else {
            Date date = new Date(data.getTimestamp() * 1000);
            postRepository.savePost(data.getActive(), data.getText(), formatter.format(date),
                                    data.getTitle(), user.getId());
        }

        tagCheckAndSave(data.getTags(), postRepository.findNewPostId(data.getText(), data.getTitle(), user.getId()));
        response.setResult(true);
        return response;

    }


    public PostResponseErrors editPost(User user, PostPublishDTO data, Integer postId) {

        PostResponseErrors response = checkPostValidity(data);

        if (response != null && !response.getResult()) {
            return response;
        }

        Post post = postRepository.findDetailPost(postId);

        if (user.getId() == post.getUser().getId()) {

            post.setModerationStatus("NEW");
            post.setTitle(data.getTitle());
            post.setText(data.getText());

            if (data.getTimestamp() <= System.currentTimeMillis()) {
                post.setTime(new Date());
            }
            else {
                post.setTime(new Date(data.getTimestamp()));
            }

            tagCheckAndSave(data.getTags(), postId);

            }
        else if (user.getIsModerator() == 1) {

            post.setTitle(data.getTitle());
            post.setText(data.getText());

            if (data.getTimestamp() <= System.currentTimeMillis()) {
                post.setTime(new Date());
            }
            else {
                post.setTime(new Date(data.getTimestamp()));
            }
        }

        response.setResult(true);
        return response;
    }

    public void tagCheckAndSave(List<String> tags, Integer postId) {
        for (String tag : tags) {
            if (tagRepository.findTagIdFromName(tag) != null) {
                tagToPostRepository.saveTagToPost(postId, tagRepository.findTagIdFromName(tag));
            }
            else {
                tagRepository.saveTag(tag);
                tagToPostRepository.saveTagToPost(postId, tagRepository.findTagIdFromName(tag));
            }
        }
    }


    public PostResponseErrors sendComment(User user, PostSendCommentDTO comment) {

        return null;
    }

    public PostResponseErrors checkPostValidity(PostPublishDTO data) {
        Map<String, String> error = new HashMap<>();
        PostResponseErrors response = new PostResponseErrors();

        if (data.getTitle().length() < 3) {
            response.setResult(false);
            response.setErrors(error.put("title", "Заголовок не установлен"));
        }

        else if (data.getText().length() < 50) {
            response.setResult(false);
            response.setErrors(error.put("text", "Текст публикации слишком короткий"));
        }

        return response;

    }
}
