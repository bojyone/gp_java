package main.controller;

import main.api.response.InitResponse;
import main.model.repositories.GlobalSettingRepository;
import main.model.repositories.UserRepository;
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
    private final GeneralService generalService;


    @Autowired
    public ApiGeneralController(InitResponse initResponse, GeneralService generalService) {
        this.initResponse = initResponse;
        this.generalService = generalService;
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

    @GetMapping("/api/tag/")
    public ResponseEntity getTag(@RequestParam(required = false) String tag) {
        return new ResponseEntity(generalService.getTagWeight(), HttpStatus.OK);
    }

    @GetMapping("/api/settings/")
    public ResponseEntity getGlobalSettings() {
        return new ResponseEntity(generalService.getSettings(), HttpStatus.OK);
    }
}
