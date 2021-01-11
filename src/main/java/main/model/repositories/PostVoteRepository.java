package main.model.repositories;

import main.model.entities.PostVote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {

    @Query(value = "SELECT pv.* FROM post_votes pv WHERE post_id = :post_id and user_id = :user_id",
           nativeQuery = true)
    PostVote findUserPostVote(@Param("post_id") Integer postId,
                              @Param("user_id") Integer userId);


    @Query(value = "SELECT COUNT(*) cnt FROM post_votes pv WHERE post_id = :post_id AND value = 1",
            nativeQuery = true)
    Integer findLikeCount(@Param("post_id") Integer postId);


    @Query(value = "SELECT COUNT(*) cnt FROM post_votes pv WHERE post_id = :post_id AND value = 0",
            nativeQuery = true)
    Integer findDislikeCount(@Param("post_id") Integer postId);


    @Modifying
    @Query(value = "INSERT INTO post_votes (user_id, post_id, time, value) VALUES (:user_id, :post_id, :time, :value)",
           nativeQuery = true)
    @Transactional
    void savePostVote(@Param("user_id") Integer userId,
                      @Param("post_id") Integer postId,
                      @Param("time") String time,
                      @Param("value") Short value);
}
