package main.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostIdDTO {

    @JsonProperty("post_id")
    private Integer postId;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }
}
