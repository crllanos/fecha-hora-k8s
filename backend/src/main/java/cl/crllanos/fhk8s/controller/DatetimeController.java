package cl.crllanos.fhk8s.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // TODO cambiar por @Configuration granular
public class DatetimeController {

    private static final String APY_KEY = "b4tm4n-b4tm4n-b4tm4n-b4tm4n";  // Sonar, OWASP Check

    @GetMapping("/datetime")
    public Map<String, String> now() {
        System.out.println("APY KEY: %s".formatted(APY_KEY));
        LocalDateTime now = LocalDateTime.now();
        return Map.of("datetime", now.toString());
    }
}