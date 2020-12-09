package main.model.repositories;

import main.model.entities.CaptchaCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {

    @Query(value = "DELETE c.* FROM captcha_codes c WHERE (UNIX_TIMESTAMP() - " +
           "UNIX_TIMESTAMP(c.time)) > 3600",
           nativeQuery = true)
    List<CaptchaCode> deleteOldCaptcha();


    @Query(value = "SELECT c.* FROM captcha_codes c WHERE c.code = :code " +
           "AND c.secret_code = :secret_code",
           nativeQuery = true)
    CaptchaCode findCaptchaCode(@Param("code") String code,
                                @Param("secret_code") String secretCode);
}
