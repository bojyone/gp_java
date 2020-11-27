package main.controller;

import main.api.response.InitResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {
    private final InitResponse initResponse;

    public ApiGeneralController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @GetMapping("/api/init/")
    private InitResponse init(@RequestBody InitResponse initResponse) {
        return initResponse;
    }
}
