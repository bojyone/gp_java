package main.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSendCommentDTO {

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("post_id")
    private Integer postId;

    private String text;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
