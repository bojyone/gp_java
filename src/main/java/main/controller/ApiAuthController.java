package main.controller;

import main.model.DTO.*;
import main.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ApiAuthController {

    private final AuthService authService;

    @Autowired
    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/api/auth/captcha")
    public ResponseEntity getCaptcha() {

        return new ResponseEntity(authService.captchaGenerate(), HttpStatus.OK);
    }


    @PostMapping("/api/auth/register")
    public ResponseEntity registration(@RequestBody NewUserDTO userData)
    {
        PostResponseErrors rr = authService.newUserRegistration(userData);

        if (rr.getResult()) {
            return new ResponseEntity(rr, HttpStatus.CREATED);
        }

        else {
            return new ResponseEntity(rr, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity authorization(@RequestParam(name = "e_mail") String email,
                                        @RequestParam String password) {

        PostResponseUser authResponse = authService.userAuthorization(email, password);
        if (authResponse.isResult()) {
            return new ResponseEntity(authResponse, HttpStatus.OK);
        }
        return new ResponseEntity(new SimpleResponse(false), HttpStatus.OK);
    }


    @GetMapping("/api/auth/check")
    public ResponseEntity authorizationCheck(@RequestParam String token) {
        
        PostResponseUser response = authService.userAuthorizationCheck(token);
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
    public ResponseEntity putNewPassword(@RequestBody UserNewPassword data) {
        PostResponseErrors response = authService.passwordChange(data);
        if (response.getResult()) {
            return new ResponseEntity(new SimpleResponse(true), HttpStatus.OK);
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
