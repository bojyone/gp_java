package main.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModeratorAction {

    @JsonProperty("post_id")
    private Integer postId;

    private String decision;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
