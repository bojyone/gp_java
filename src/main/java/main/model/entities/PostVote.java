package main.model.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    @JoinColumn(name = "user_id", nullable=false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    @JoinColumn(name = "post_id", nullable=false)
    private Post post;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Short value;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }
}
