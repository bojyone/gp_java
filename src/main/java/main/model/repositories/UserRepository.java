package main.model.repositories;

import main.model.entities.Post;
import main.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT u.* FROM users u WHERE u.email = :email",
           nativeQuery = true)
    User findUserFromEmail(@Param("email") String email);


    @Query(value = "SELECT u.* FROM users u WHERE u.id = :id",
           nativeQuery = true)
    User findUserFromId(@Param("id") Integer id);


    @Query(value = "SELECT u.* FROM users u WHERE u.code = :code",
           nativeQuery = true)
    User findUserFromCode(@Param("code") String code);


    @Modifying
    @Query(value = "INSERT INTO users (email, is_moderator, name, password, reg_time) " +
            "VALUES (:e_mail, :is_moderator, :name, :password, :reg_time)",
            nativeQuery = true)
    @Transactional
    void saveUser(@Param("e_mail") String email,
                  @Param("is_moderator") Short isModerator,
                  @Param("name") String name,
                  @Param("password") String password,
                  @Param("reg_time") String regTime);


    @Modifying
    @Query(value = "UPDATE users SET code = :code WHERE id = :id",
           nativeQuery = true)
    @Transactional
    void updateUserCode(@Param("code") String code,
                        @Param("id") Integer id);
}
