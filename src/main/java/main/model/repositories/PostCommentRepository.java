package main.model.repositories;

import main.model.entities.PostComment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {

    @Modifying
    @Query(value = "INSERT INTO post_comments (text, time, post_id, user_id, parent_id) " +
           "VALUES (:text, :time, :post_id, :user_id, :parent_id)",
           nativeQuery = true)
    @Transactional
    void saveComment(@Param("text") String text,
                     @Param("time") String time,
                     @Param("post_id") Integer postId,
                     @Param("user_id") Integer userId,
                     @Param("parent_id") Integer parentId);


    @Query(value = "SELECT MAX(x.num) FROM (SELECT p.id num FROM post_comments p WHERE p.post_id = :post_id " +
            "AND p.text = :text AND p.user_id = :user_id AND parent_id = :parent_id) x",
            nativeQuery = true)
    Integer findNewCommentId(@Param("text") String text,
                             @Param("post_id") Integer postId,
                             @Param("user_id") Integer userID,
                             @Param("parent_id") Integer parentId);


    @Query(value = "SELECT p.* FROM post_comments p WHERE id = :id",
           nativeQuery = true)
    PostComment findCommentFromId(@Param("id") Integer id);

}
