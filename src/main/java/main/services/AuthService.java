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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository ccRepository;
    private final PostRepository postRepository;
    private final JavaMailSender emailSender;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final Map<String, Integer> authUsers = new HashMap<>();
    private final Integer MIN_PASSWORD_SIZE = 6;



    @Autowired
    public AuthService(UserRepository userRepository, CaptchaCodeRepository ccRepository,
                       PostRepository postRepository, JavaMailSender emailSender) {

        this.userRepository = userRepository;
        this.ccRepository = ccRepository;
        this.postRepository = postRepository;
        this.emailSender = emailSender;
    }


    public CaptchaDTO captchaGenerate() {

        ccRepository.deleteOldCaptcha();

        Cage cage = new GCage();

        String secret_code = cage.getTokenGenerator().next();
        byte[] fileContent = cage.draw(secret_code);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        String code = "data:image/png;base64, " + encodedString;


        ccRepository.saveCaptcha(formatter.format(new Date()), code, secret_code);


        return convertCaptchaCodeToCaptchaDTO(ccRepository.findCaptchaCode(secret_code));
    }


    private CaptchaDTO convertCaptchaCodeToCaptchaDTO(CaptchaCode cc) {
        CaptchaDTO captcha = new CaptchaDTO();

        captcha.setSecret(cc.getSecretCode());
        captcha.setImage(cc.getCode());

        return captcha;
    }


    public boolean captchaCheck(String requestSecretCaptcha) {
        return ccRepository.findCaptchaCode(requestSecretCaptcha) != null;
    }


    public PostResponseErrors newUserRegistration(UserRegisterDTO userData) {
        PostResponseErrors registrationResponse = new PostResponseErrors();
        Map<String, String> error = new HashMap<>();

        String requestCaptcha = userData.getCaptcha();

        if (userRepository.findUserFromEmail(userData.getEmail()) != null) {
            registrationResponse.setResult(false);
            error.put("email", "Этот e-mail уже зарегистрирован");
            registrationResponse.setErrors(error);
            return registrationResponse;
        }

        else if (userData.getPassword().length() < MIN_PASSWORD_SIZE) {
            registrationResponse.setResult(false);
            error.put("password", "Пароль короче " + MIN_PASSWORD_SIZE + " символов");
            registrationResponse.setErrors(error);
            return registrationResponse;
        }

        else if (!captchaCheck(requestCaptcha)) {
            registrationResponse.setResult(false);
            error.put("captcha", "Код с картинки введён неверно");
            registrationResponse.setErrors(error);
            return registrationResponse;
        }

        else if (!userData.getName().matches("[a-zA-Zа-яА-Я ]*")) {
            registrationResponse.setResult(false);
            error.put("name", "Имя указано неверно");
            registrationResponse.setErrors(error);
            return registrationResponse;
        }

        userRepository.saveUser(userData.getEmail(), (short) 0, userData.getName(), passwordEncoder.encode(userData.getPassword()), formatter.format(new Date()));
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

            authResponse.setResult(true);
            authResponse.setUser(userDetail);
        }
        return authResponse;
    }


    public String getGeneratedToken(Integer size) {

        byte[] randomBytes = new byte[size];
        secureRandom.nextBytes(randomBytes);
        String token = base64Encoder.encodeToString(randomBytes);
        return token;
    }


    public boolean sessionCheck(String sessionId) {

        return authUsers.containsKey(sessionId);
    }


    public void saveSession(String sessionId, Integer userId) {

        authUsers.put(sessionId, userId);
    }


    public void removeSession(String sessionId) {
        try {
            authUsers.remove(sessionId);
        }
        catch (Exception ignored) { }
    }


    public User getUserFromSessionId(String sessionId) {

        return userRepository.findUserFromId(authUsers.get(sessionId));
    }



    public PostResponseUser userAuthorizationCheck(String sessionId) {
        PostResponseUser response = new PostResponseUser();
        response.setResult(false);

        if (authUsers.containsKey(sessionId)) {

            User user = userRepository.findUserFromId(authUsers.get(sessionId));

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

            String code = getGeneratedToken(45);
            userRepository.updateUserCode(code, user.getId());

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("Восстановление пароля");
            message.setText("Для восстановления пароля пройдите по данной ссылке: " +
                            "http://127.0.0.1:8080/login/change-password/" + code);
            emailSender.send(message);

            return new SimpleResponse(true);
        }
        return new SimpleResponse(false);
    }


    public PostResponseErrors passwordCheckAndChange(UserNewPassword data) {

        User user = userRepository.findUserFromCode(data.getCode());
        Map<String, String> error = new HashMap<>();
        PostResponseErrors response = new PostResponseErrors();
        response.setResult(true);

        if (user == null) {
            response.setResult(false);
            error.put("code", "Ссылка для восстановления пароля устарела.<a href=\"/auth/restore\">" +
                      "Запросить ссылку снова</a>");
            response.setErrors(error);
            return response;
        }
        else if (data.getPassword().length() < MIN_PASSWORD_SIZE) {
            response.setResult(false);
            error.put("password", "Пароль короче " + MIN_PASSWORD_SIZE + " символов");
            response.setErrors(error);
            return response;
        }
        else if (!captchaCheck(data.getCaptcha())) {
            response.setResult(false);
            error.put("captcha", "Код с картинки введён неверно");
            response.setErrors(error);
            return response;
        }

        user.setPassword(passwordEncoder.encode(data.getPassword()));

        return response;
    }

}
