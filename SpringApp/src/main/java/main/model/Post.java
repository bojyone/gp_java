package main.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
    private Short isActive;

    @Column(name = "moderation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    @JoinColumn(name = "moderator_id", nullable = true)
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostVote> postVotes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Tag2Post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> postTags;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostComment> postComments;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Short getIsActive() {
        return isActive;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(List<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public List<Tag> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<Tag> postTags) {
        this.postTags = postTags;
    }

    public List<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(List<PostComment> postComments) {
        this.postComments = postComments;
    }
}