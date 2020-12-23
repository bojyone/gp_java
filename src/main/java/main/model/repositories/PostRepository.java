package main.model.repositories;

import main.model.DTO.*;
import main.model.entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(*) FROM posts p WHERE p.is_active = 1 AND p.moderation_" +
           "status = 'ACCEPTED' AND time <= (SELECT NOW())",
           nativeQuery = true)
    Integer findAllActivePostsCount();


    @Query(value = "SELECT p.* FROM posts p WHERE p.is_active = 1 AND p.moderation_" +
            "status = 'ACCEPTED' AND time <= (SELECT NOW()) ORDER BY time DESC",
            nativeQuery = true)
    List<Post> findRecentPosts(@Param("paging") Pageable paging);


    @Query(value = "SELECT p.* FROM posts p WHERE p.is_active = 1 AND p.moderation_" +
            "status = 'ACCEPTED' AND time <= (SELECT NOW()) ORDER BY time ASC",
            nativeQuery = true)
    List<Post> findEarlyPosts(@Param("paging") Pageable paging);


    @Query(value = "SELECT x.* FROM  (SELECT p.*, COUNT(pc1.id) AS cnt FROM posts p " +
           "LEFT JOIN (SELECT pc.* FROM post_comments pc) pc1 ON p.id = pc1.post_id GROUP BY p.id " +
           "HAVING is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= (SELECT NOW())) x " +
           "ORDER BY x.cnt DESC",
           nativeQuery = true)
    List<Post> findPopularPosts(@Param("paging") Pageable paging);


    @Query(value = "SELECT x.* FROM (SELECT p.*, COUNT(pv1.id) AS cnt FROM posts p " +
           "LEFT JOIN (SELECT pv.* FROM post_votes pv WHERE value = 1) pv1 ON p.id = pv1.post_id GROUP BY p.id " +
           "HAVING is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= " +
           "(SELECT NOW())) x ORDER BY x.cnt DESC",
           nativeQuery = true)
    List<Post> findBestPosts(@Param("paging") Pageable paging);


    @Query(value = "SELECT p.* FROM posts p WHERE (p.moderation_status = 'NEW' OR p.moderator_id = :moderator_id) " +
            "AND p.is_active = 1",
            nativeQuery = true)
    List<Post> findModeratorPosts(@Param("paging") Pageable paging,
                                  @Param("moderator_id") Integer id);

    @Query(value = "SELECT p.* FROM posts p WHERE p.id = :id",
            nativeQuery = true)
    Post findDetailPost(@Param("id") Integer id);


    @Query(value = "SELECT p.* FROM posts p JOIN users u ON p.user_id = u.id WHERE is_active = 1 " +
           "AND moderation_status = 'ACCEPTED' AND time <= (SELECT NOW()) AND (:query LIKE (%u.name%)" +
           " OR p.title LIKE (%:query%) OR :query = u.id)",
           nativeQuery = true)
    List<Post> findSearchResult(@Param("paging") Pageable paging,
                                @Param("query") String query);


    @Query(value = "SELECT p.* FROM posts p WHERE p.is_active = 1 AND p.moderation_" +
           "status = 'ACCEPTED' AND p.time <= (SELECT NOW()) AND DATE(p.time) = :date",
           nativeQuery = true)
    List<Post> findSearchResultByDate(@Param("paging") Pageable paging,
                                      @Param("date") String date);


    @Query(value = "SELECT p.* FROM posts p JOIN tag2post tp JOIN tags t ON p.id = tp.post_id AND " +
            "tp.tag_id = t.id WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time <= (SELECT NOW()) AND t.name LIKE (%:tag%)",
            nativeQuery = true)
    List<Post> findSearchResultByTag(@Param("paging") Pageable paging,
                                     @Param("tag") String tag);


//    @Query(value = "SELECT DISTINCT x.p_date FROM (SELECT DATE(p.time) AS p_date " +
//           "FROM posts AS p WHERE YEAR(p.time) = :year) AS x",
//           nativeQuery = true)
//    List<String> findDatePostOfYear(@Param("year") Integer year);


    @Query(value = "SELECT x.p_date as date, COUNT(x.p_id) as count FROM (SELECT DATE(p.time) AS p_date, " +
           "p.id AS p_id FROM posts AS p WHERE YEAR(p.time) = :year) AS x GROUP BY x.p_date",
           nativeQuery = true)
    List<PostCountInterface> findPostCountOfYear(@Param("year") Integer year);


    @Query(value = "SELECT DISTINCT YEAR(p.time) FROM posts AS p",
           nativeQuery = true)
    List<Integer> findYears();


    @Query(value = "SELECT COUNT(p.*) FROM posts p WHERE p.moderation_status = 'NEW'",
           nativeQuery = true)
    Integer findCountNewPosts();


    @Query(value = "SELECT COUNT(*) postsCount, (SELECT COUNT(*) FROM posts p1 JOIN post_votes " +
            "pv1 ON p1.id = pv1.post_id WHERE value = 1) likesCount,  (SELECT COUNT(*) FROM posts p1 " +
            "JOIN post_votes pv1 ON p1.id = pv1.post_id WHERE value = 0) dislikesCount, SUM(view_count) viewsCount, " +
            "UNIX_TIMESTAMP(MIN(time)) firstPublication FROM posts",
           nativeQuery = true)
    StatisticInterface findAllStatistics();


    @Query(value = "SELECT COUNT(*) postsCount, (SELECT COUNT(*) FROM posts p1 JOIN post_votes " +
            "pv1 ON p1.id = pv1.post_id WHERE value = 1 AND p1.user_id = :id) likesCount,  (SELECT COUNT(*) FROM " +
            "posts p1 JOIN post_votes pv1 ON p1.id = pv1.post_id WHERE value = 0 AND p1.user_id = :id) dislikesCount," +
            " SUM(view_count) viewsCount, UNIX_TIMESTAMP(MIN(time)) firstPublication FROM posts WHERE user_id = :id",
            nativeQuery = true)
    StatisticInterface findUserStatistics(@Param("id") Integer userId);


    @Query(value = "SELECT p.* FROM posts p WHERE user_id = :id AND p.is_active = 0",
           nativeQuery = true)
    List<Post> findUserInactivePosts(@Param("id") Integer userId,
                                     @Param("paging") Pageable paging);



    @Query(value = "SELECT p.* FROM posts p WHERE user_id = :id AND p.is_active = 1 AND moderation_status = 'NEW'",
           nativeQuery = true)
    List<Post> findUserPendingPosts(@Param("id") Integer userId,
                                    @Param("paging") Pageable paging);



    @Query(value = "SELECT p.* FROM posts p WHERE user_id = :id AND p.is_active = 1 AND moderation_status = 'DECLINED'",
           nativeQuery = true)
    List<Post> findUserDeclinedPosts(@Param("id") Integer userId,
                                     @Param("paging") Pageable paging);


    @Query(value = "SELECT p.* FROM posts p WHERE user_id = :id AND p.is_active = 1 AND moderation_status = 'ACCEPTED'",
           nativeQuery = true)
    List<Post> findUserPublishedPosts(@Param("id") Integer userId,
                                      @Param("paging") Pageable paging);


    @Modifying
    @Query(value = "INSERT INTO posts (is_active, text, time, title, user_id) VALUES (" +
           ":is_active, :text, :time, :title, :user_id)",
           nativeQuery = true)
    @Transactional
    void savePost(@Param("is_active") Short isActive,
                  @Param("text") String text,
                  @Param("time") String time,
                  @Param("title") String title,
                  @Param("user_id") Integer userId);


    @Query(value = "SELECT MAX(x.num) FROM (SELECT p.id num FROM posts p WHERE p.title = :title " +
           "AND p.text = :text AND p.user_id = :user_id) x",
           nativeQuery = true)
    Integer findNewPostId(@Param("text") String text,
                          @Param("title") String title,
                          @Param("user_id") Integer userID);

}
