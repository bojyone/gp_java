package main.model.repositories;

import main.model.entities.Tag2Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {

    @Modifying
    @Query(value = "INSERT INTO tag2post (post_id, tag_id) VALUES (:post_id, :tag_id)",
           nativeQuery = true)
    @Transactional
    void saveTagToPost(@Param("post_id") Integer postId,
                       @Param("tag_id") Integer tagId);
}
