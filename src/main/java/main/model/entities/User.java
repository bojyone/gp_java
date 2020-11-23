package main.model.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "is_moderator", nullable = false, columnDefinition = "TINYINT")
    private Short isModerator;

    @Column(name = "reg_time", nullable = false)
    private Date regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String code;

    @Column(length = 1000, nullable = true)
    private String photo;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Post> userPosts;


    @OneToMany
    @JoinColumn(name = "moderator_id")
    private List<Post> moderatorPosts;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<PostVote> userVotes;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<PostComment> userComments;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Short getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(Short isModerator) {
        this.isModerator = isModerator;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Post> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(List<Post> userPosts) {
        this.userPosts = userPosts;
    }

    public List<Post> getModeratorPosts() {
        return moderatorPosts;
    }

    public void setModeratorPosts(List<Post> moderatorPosts) {
        this.moderatorPosts = moderatorPosts;
    }

    public List<PostVote> getUserVotes() {
        return userVotes;
    }

    public void setUserVotes(List<PostVote> userVotes) {
        this.userVotes = userVotes;
    }

    public List<PostComment> getUserComments() {
        return userComments;
    }

    public void setUserComments(List<PostComment> userComments) {
        this.userComments = userComments;
    }
}
