package main.model.DTO;

public class PostResponseUser {
    private boolean result;
    private UserDetailDTO user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserDetailDTO getUser() {
        return user;
    }

    public void setUser(UserDetailDTO user) {
        this.user = user;
    }
}
