package main.model.repositories;

import main.model.entities.Post;
import main.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT u.* FROM users u WHERE u.email = :email",
           nativeQuery = true)
    User findUserFromEmail(@Param("email") String email);

}
