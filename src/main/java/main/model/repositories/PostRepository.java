package main.model.repositories;

import main.model.entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(*) FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED'",
           nativeQuery = true)
    Integer findAllActivePostsCount();


    @Query(value = "SELECT p.* FROM posts p ORDER BY time DESC",
            nativeQuery = true)
    List<Post> findRecentPosts(@Param("paging") Pageable paging);

    @Query(value = "SELECT p.* FROM posts p ORDER BY time ASC",
            nativeQuery = true)
    List<Post> findEarlyPosts(@Param("paging") Pageable paging);

    @Query(value = "SELECT x.* FROM  (SELECT p.*, COUNT(pc.post_id) AS cnt FROM posts p " +
           " JOIN post_comments pc ON p.id = pc.post_id GROUP BY pc.post_id " +
           "HAVING is_active = 1 AND moderation_status = 'ACCEPTED') x " +
           "ORDER BY x.cnt DESC",
           nativeQuery = true)
    List<Post> findPopularPosts(@Param("paging") Pageable paging);


    @Query(value = "SELECT x.* FROM (SELECT p.*, COUNT(pv.post_id) AS cnt, value FROM posts p " +
           "JOIN post_votes pv ON p.id = pv.post_id GROUP BY pv.post_id, pv.value " +
           "HAVING is_active = 1 AND moderation_status = 'ACCEPTED' AND value=1) x " +
           "ORDER BY x.cnt DESC",
           nativeQuery = true)
    List<Post> findBestPosts(@Param("paging") Pageable paging);
}
