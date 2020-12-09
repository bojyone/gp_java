package main.services;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.model.DTO.CaptchaDTO;
import main.model.entities.CaptchaCode;
import main.model.entities.User;
import main.model.repositories.CaptchaCodeRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository ccRepository;

    @Autowired
    public AuthService(UserRepository userRepository, CaptchaCodeRepository ccRepository) {

        this.userRepository = userRepository;
        this.ccRepository = ccRepository;
    }

    public boolean checkUser(String email)
    {
        if (userRepository.findUserFromEmail(email) == null)
            return true;
        return false;
    }

    public User getUserFromEmail(String email)
    {
        return userRepository.findUserFromEmail(email);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CaptchaDTO captchaGenerate() {
        Cage cage = new GCage();

        String code = cage.getTokenGenerator().next();
        byte[] fileContent = cage.draw(code);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        CaptchaCode cc = new CaptchaCode();
        cc.setCode(code);
        cc.setSecretCode("data:image/png;base64, " + encodedString);
        cc.setTime(new Date());

        ccRepository.save(cc);
        ccRepository.deleteOldCaptcha();

        return convertCaptchaCodeToCaptchaDTO(cc);
    }


    private CaptchaDTO convertCaptchaCodeToCaptchaDTO(CaptchaCode cc) {
        CaptchaDTO captcha = new CaptchaDTO();

        captcha.setSecret(cc.getSecretCode());
        captcha.setImage(cc.getCode());

        return captcha;
    }

    public boolean checkCaptcha(String requestCaptcha, String requestSecretCaptcha) {
        if (ccRepository.findCaptchaCode(requestCaptcha, requestSecretCaptcha) != null)
            return true;
        return false;
    }
}
