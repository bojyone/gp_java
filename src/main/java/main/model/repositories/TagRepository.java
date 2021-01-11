package main.model.repositories;

import main.model.DTO.TagWeightInterface;
import main.model.entities.Tag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Integer> {

    @Query(value = "SELECT x.n name, CASE WHEN x.part = MAX(x.part) THEN 1.0 ELSE x.part / MAX(x.part) " +
           "END weight FROM (SELECT t.name n, COUNT(p.id) cnt, COUNT(p.id) / (SELECT COUNT(*) " +
           "FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' " +
           "AND time <= (SELECT NOW())) part FROM tags t JOIN posts p JOIN tag2post tp " +
           "ON tp.post_id = p.id AND tp.tag_id = t.id GROUP BY n) x GROUP BY 1",
           nativeQuery = true)
    List<TagWeightInterface> findAllTagWeight();


    @Query(value = "SELECT y.* FROM (SELECT x.n name, CASE WHEN x.part = MAX(x.part) THEN 1.0 ELSE x.part / MAX(x.part) " +
            "END weight FROM (SELECT t.name n, COUNT(p.id) cnt, COUNT(p.id) / (SELECT COUNT(*) " +
            "FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' " +
            "AND time <= (SELECT NOW())) part FROM tags t JOIN posts p JOIN tag2post tp " +
            "ON tp.post_id = p.id AND tp.tag_id = t.id GROUP BY n) x GROUP BY 1) y WHERE y.name LIKE (%:query%)",
            nativeQuery = true)
    List<TagWeightInterface> findTagWeight(@Param("query") String query);


    @Query(value = "SELECT t.id FROM tags t WHERE t.name = :name",
           nativeQuery = true)
    Integer findTagIdFromName(@Param("name") String name);


    @Modifying
    @Query(value = "INSERT INTO tags (name) VALUES (:name)",
           nativeQuery = true)
    @Transactional
    void saveTag(@Param("name") String name);
}
