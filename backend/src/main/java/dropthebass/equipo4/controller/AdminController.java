package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.UserDTO;
import dropthebass.equipo4.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administracion")
public class AdminController {
    private final IAuthService authService;

    @Autowired
    public AdminController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/promote")
    public ResponseEntity<String> promoteToAdmin(@RequestParam String emailUser) {
        boolean isPromoted = authService.promoteToAdmin(emailUser);

        if (isPromoted) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuario promovido a administrador");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo promover al usuario");
        }
    }

    @PostMapping("/change-user")
    public ResponseEntity<String> changeToUser(@RequestParam String emailUser) {
        boolean isChangeToUser = authService.changeToUser(emailUser);

        if (isChangeToUser) {
            return ResponseEntity.status(HttpStatus.OK).body("Se ha modificado el rol de administrador a rol a usuario");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo cambiar el rol del administrador");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        try {
            List<UserDTO> userDTOS = authService.listUsers();
            return ResponseEntity.ok(userDTOS);
        } catch (DataAccessException e ) {
            String errorMessage = "Error al listar los usuarios debido a un problema en la base de datos: ";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
        }

    }
}
