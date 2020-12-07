package main.model.repositories;

import main.model.entities.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query(value = "SELECT p.* FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED'",
           nativeQuery = true)
    List<Post> findAllActivePosts();

    @Query(value = "SELECT x.* FROM (SELECT p.*, ROW_NUMBER() OVER (ORDER BY p.time DESC) " +
            "AS row_num  FROM posts AS p WHERE p.is_active = 1 AND p.moderation_status " +
            "= 'ACCEPTED') AS x WHERE x.row_num BETWEEN :offset AND :limit",
            nativeQuery = true)
    List<Post> findLimitRecentPosts(@Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    @Query(value = "SELECT x.* FROM (SELECT p.*, ROW_NUMBER() OVER (ORDER BY p.time ASC) " +
            "AS row_num  FROM posts AS p WHERE p.is_active = 1 AND p.moderation_status " +
            "= 'ACCEPTED') AS x WHERE x.row_num BETWEEN :offset AND :limit",
            nativeQuery = true)
    List<Post> findLimitEarlyPosts(@Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    @Query(value = "SELECT y.* FROM (SELECT x.*, ROW_NUMBER() OVER (ORDER BY x.cnt DESC) AS row_num FROM " +
           "(SELECT p.*, COUNT(pc.post_id) AS cnt FROM posts p JOIN post_comments pc " +
           "ON p.id = pc.post_id GROUP BY pc.post_id HAVING is_active = 1 " +
           "AND moderation_status = 'ACCEPTED') x ) y WHERE y.row_num BETWEEN :offset AND :limit",
           nativeQuery = true)
    List<Post> findLimitPopularPosts(@Param("offset") Integer offset,
                                     @Param("limit") Integer limit);


    @Query(value = "SELECT y.* FROM (SELECT x.*, ROW_NUMBER() OVER (ORDER BY x.cnt DESC) AS row_num FROM " +
           "(SELECT p.*, COUNT(pv.post_id) AS cnt, value FROM posts p LEFT JOIN post_votes pv " +
           "ON p.id = pv.post_id GROUP BY pv.post_id, pv.value HAVING is_active = 1 " +
           "AND moderation_status = 'ACCEPTED' AND value=1) x ) y WHERE y.row_num BETWEEN :offset AND :limit",
           nativeQuery = true)
    List<Post> findLimitBestPosts(@Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
}
