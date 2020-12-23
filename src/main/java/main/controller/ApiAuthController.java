package main.controller;

import main.model.DTO.*;
import main.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;


@RestController
public class ApiAuthController {

    private final AuthService authService;

    @Autowired
    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/api/auth/captcha")
    public ResponseEntity captcha() {

        return new ResponseEntity(authService.captchaGenerate(), HttpStatus.OK);
    }


    @PostMapping("/api/auth/register")
    public ResponseEntity registration(@RequestBody UserRegisterDTO userData)
    {
        PostResponseErrors registrationResponse = authService.newUserRegistration(userData);

        if (registrationResponse.getResult()) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.CREATED);
        }

        return new ResponseEntity(registrationResponse, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/api/auth/login")
    public ResponseEntity authorization(@RequestBody UserAuthDTO data) {

        PostResponseUser authResponse = authService.userAuthorization(data.getEmail(), data.getPassword());

        authService.saveSession(RequestContextHolder.currentRequestAttributes().getSessionId(), authResponse.getUser().getId());

        if (authResponse.isResult()) {
            return new ResponseEntity(authResponse, HttpStatus.OK);
        }
        return new ResponseEntity(new SimpleResponse(false), HttpStatus.OK);
    }


    @GetMapping("/api/auth/logout")
    public ResponseEntity logout() {

        authService.removeSession(RequestContextHolder.currentRequestAttributes().getSessionId());
        return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
    }


    @GetMapping("/api/auth/check")
    public ResponseEntity authorizationCheck() {
        
        PostResponseUser response = authService.userAuthorizationCheck(RequestContextHolder.currentRequestAttributes().getSessionId());
        if (response.isResult()) {
            return new ResponseEntity(response, HttpStatus.OK);
        }
        return new ResponseEntity(new SimpleResponse(false), HttpStatus.OK);
    }


    @PostMapping("/api/auth/restore")
    public ResponseEntity passwordRestore(String email) {

        return new ResponseEntity(authService.userPasswordRestore(email), HttpStatus.OK);
    }


    @PostMapping("/api/auth/password")
    public ResponseEntity passwordChange(@RequestBody UserNewPassword data) {
        PostResponseErrors response = authService.passwordCheckAndChange(data);

        if (response.getResult()) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
