package dropthebass.equipo4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public String home() {
        return "Bienvenidos a Drop The Bass";
    }

    @GetMapping("/admin")
    public String homeAdmin() {
        return "Bienvenidos a Drop The Bass (ADMIN)";
    }

    @GetMapping("/user")
    public String homeUser() {
        return "Bienvenidos a Drop The Bass (USER)";
    }



}
