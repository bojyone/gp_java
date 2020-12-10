package main.controller;

import main.api.response.InitResponse;
import main.services.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
public class ApiGeneralController {
    private final InitResponse initResponse;


    @Autowired(required=true)
    private GeneralService generalService;


    public ApiGeneralController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }


    @GetMapping("/api/init/")
    private InitResponse init(@RequestBody InitResponse initResponse) {
        return initResponse;
    }


    @GetMapping("/api/calendar/")
    public ResponseEntity getCalendar(@RequestParam(required = false) Integer year) {


        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);

        return new ResponseEntity(generalService.getPostCalendar(year), HttpStatus.OK);
    }
}
