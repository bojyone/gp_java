package main.controller;

import main.model.DTO.NewUserDTO;
import main.model.entities.User;
import main.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiAuthController {

    private Map<String, Integer> authUsers = new HashMap<>();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Autowired
    private AuthService authService;


    @GetMapping("/api/auth/captcha")
    public ResponseEntity getCaptcha() {

        return new ResponseEntity(authService.captchaGenerate(), HttpStatus.OK);
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity registration(@RequestBody NewUserDTO userData)
    {
        String requestCaptcha = userData.getCaptcha();
        String requestSecretCaptcha = userData.getCaptchaSecret();

        if (!authService.checkUser(userData.getEmail()))
            return new ResponseEntity("{\"result\": false, \"errors\": {" +
                    "\"email\": \"Этот e-mail уже зарегистрирован\"}}", HttpStatus.BAD_REQUEST);

        else if (userData.getPassword().length() < 6)
            return new ResponseEntity("{\"result\": false, \"errors\": {" +
                    "\"password\": \"Пароль короче 6-ти символов\"}}", HttpStatus.BAD_REQUEST);

        else if (!authService.checkCaptcha(requestCaptcha, requestSecretCaptcha))
            return new ResponseEntity("{\"result\": false, \"errors\": {" +
                    "\"captcha\": \"Код с картинки введён неверно\"}}", HttpStatus.BAD_REQUEST);

        else if (!userData.getName().matches("[a-zA-Zа-яА-Я ]*"))
            return new ResponseEntity("{\"result\": false, \"errors\": {" +
                    "\"name\": \"Имя указано неверно\"}}", HttpStatus.BAD_REQUEST);

        else {


            User newUser = new User();

            newUser.setName(userData.getName());
            newUser.setEmail(userData.getEmail());
            newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
            newUser.setIsModerator((short) 0);
            newUser.setRegTime(new Date());
            authService.getUserRepository().save(newUser);

            return new ResponseEntity("{\"result\": true}", HttpStatus.CREATED);
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity authorization(@RequestParam(name = "e_mail") String email,
                                        @RequestParam String password) {
        User user = authService.getUserFromEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            authUsers.put(base64Encoder.encodeToString(randomBytes), user.getId());
            return new ResponseEntity("{\"result\": true}", HttpStatus.OK);
        }
        return new ResponseEntity("{\"result\": false}", HttpStatus.OK);
    }
}
