package main.services;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.model.DTO.*;
import main.model.entities.CaptchaCode;
import main.model.entities.User;
import main.model.repositories.CaptchaCodeRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository ccRepository;
    private final PostRepository postRepository;
    private final String emailError = "\"email\": \"Этот e-mail уже зарегистрирован\"";
    private final String passwordError = "\"password\": \"Пароль короче 6-ти символов\"";
    private final String captchaError = "\"captcha\": \"Код с картинки введён неверно\"";
    private final String nameError = "\"name\": \"Имя указано неверно\"";
    private final String codeError = "\"code\": \"Ссылка для восстановления пароля устарела." +
                                     "<a href=\"/auth/restore\">Запросить ссылку снова</a>\"";

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private Map<String, Integer> authUsers = new HashMap<>();


    @Autowired
    public AuthService(UserRepository userRepository, CaptchaCodeRepository ccRepository,
                       PostRepository postRepository) {

        this.userRepository = userRepository;
        this.ccRepository = ccRepository;
        this.postRepository = postRepository;
    }

    public boolean checkUser(String email)
    {
        if (userRepository.findUserFromEmail(email) == null)
            return true;
        return false;
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
        return ccRepository.findCaptchaCode(requestCaptcha, requestSecretCaptcha) != null;
    }

    public PostResponseErrors newUserRegistration(NewUserDTO userData) {
        PostResponseErrors registrationResponse = new PostResponseErrors();

        String requestCaptcha = userData.getCaptcha();
        String requestSecretCaptcha = userData.getCaptchaSecret();

        if (!checkUser(userData.getEmail())) {
            registrationResponse.setResult(false);
            registrationResponse.setErrors(emailError);
            return registrationResponse;
        }

        else if (userData.getPassword().length() < 6) {
            registrationResponse.setResult(false);
            registrationResponse.setErrors(passwordError);
            return registrationResponse;
        }

        else if (!checkCaptcha(requestCaptcha, requestSecretCaptcha)) {
            registrationResponse.setResult(false);
            registrationResponse.setErrors(captchaError);
            return registrationResponse;
        }

        else if (!userData.getName().matches("[a-zA-Zа-яА-Я ]*")) {
            registrationResponse.setResult(false);
            registrationResponse.setErrors(nameError);
            return registrationResponse;
        }

        User newUser = new User();

        newUser.setName(userData.getName());
        newUser.setEmail(userData.getEmail());
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setIsModerator((short) 0);
        newUser.setRegTime(new Date());
        userRepository.save(newUser);
        registrationResponse.setResult(true);
        return registrationResponse;
    }

    public PostResponseUser userAuthorization(String email, String password) {
        PostResponseUser authResponse = new PostResponseUser();
        authResponse.setResult(false);
        User user = userRepository.findUserFromEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            UserDetailDTO userDetail = new UserDetailDTO();
            userDetail.setId(user.getId());
            userDetail.setName(user.getName());
            userDetail.setEmail(user.getEmail());
            userDetail.setPhoto(user.getPhoto());
            if (user.getIsModerator() == 1) {
                userDetail.setModeration(true);
                userDetail.setSettings(true);
                userDetail.setModerationCount(postRepository.findCountNewPosts());
            }
            else {
                userDetail.setModeration(false);
                userDetail.setSettings(false);
                userDetail.setModerationCount(0);
            }

            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            String token = base64Encoder.encodeToString(randomBytes);
            authUsers.put(token, user.getId());
            userDetail.setToken(token);
            authResponse.setResult(true);
            authResponse.setUser(userDetail);
        }
        return authResponse;
    }

    public PostResponseUser userAuthorizationCheck(String token) {
        PostResponseUser response = new PostResponseUser();
        response.setResult(false);

        if (authUsers.containsKey(token)) {

            User user = userRepository.findUserFromId(authUsers.get(token));

            UserDetailDTO userDetail = new UserDetailDTO();
            userDetail.setId(user.getId());
            userDetail.setName(user.getName());
            userDetail.setEmail(user.getEmail());
            userDetail.setPhoto(user.getPhoto());
            if (user.getIsModerator() == 1) {
                userDetail.setModeration(true);
                userDetail.setSettings(true);
                userDetail.setModerationCount(postRepository.findCountNewPosts());
            } else {
                userDetail.setModeration(false);
                userDetail.setSettings(false);
                userDetail.setModerationCount(0);
            }
            response.setResult(true);
            response.setUser(userDetail);
        }
        return response;

    }

    public SimpleResponse userPasswordRestore(String email) {

        User user = userRepository.findUserFromEmail(email);
        if (user != null) {

            byte[] randomBytes = new byte[45];
            secureRandom.nextBytes(randomBytes);
            user.setCode(base64Encoder.encodeToString(randomBytes));
            return new SimpleResponse(true);
        }
        return new SimpleResponse(false);
    }

    public PostResponseErrors passwordChange(UserNewPassword data) {

        User user = userRepository.findUserFromCode(data.getCode());
        PostResponseErrors response = new PostResponseErrors();
        response.setResult(true);

        if (user == null) {
            response.setResult(false);
            response.setErrors(codeError);
            return response;
        }
        else if (data.getPassword().length() < 6) {
            response.setResult(false);
            response.setErrors(passwordError);
            return response;
        }
        else if (!checkCaptcha(data.getCaptcha(), data.getCaptchaSecret())) {
            response.setResult(false);
            response.setErrors(captchaError);
            return response;
        }

        user.setPassword(passwordEncoder.encode(data.getPassword()));

        return response;
    }
}
