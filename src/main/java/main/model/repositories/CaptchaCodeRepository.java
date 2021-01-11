package main.model.repositories;

import main.model.entities.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {

    @Modifying
    @Query(value = "DELETE c.* FROM captcha_codes c WHERE (UNIX_TIMESTAMP() - " +
           "UNIX_TIMESTAMP(c.time)) > 3600",
           nativeQuery = true)
    @Transactional
    void deleteOldCaptcha();


    @Query(value = "SELECT c.* FROM captcha_codes c WHERE c.secret_code = :secret_code",
           nativeQuery = true)
    CaptchaCode findCaptchaCode(@Param("secret_code") String secret_code);


    @Modifying
    @Query(value = "INSERT INTO captcha_codes (time, code, secret_code) VALUES (:time, :code, :secret_code)",
           nativeQuery = true)
    @Transactional
    void saveCaptcha(@Param("time") String time,
                     @Param("code") String code,
                     @Param("secret_code") String secretCode);
}
