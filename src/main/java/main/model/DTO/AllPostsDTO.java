package main.model.DTO;


import java.util.List;

public class AllPostsDTO {
    private Integer count;
    private List<PostDTO> posts;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }
}
