package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.UserDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final IAuthService authService;

    @Autowired
    public UserController(IAuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> listUsers() {
        try {
            List<UserDTO> userDTOS = authService.listUsers();
            return ResponseEntity.ok(userDTOS);
        } catch (DataAccessException e ) {
            String errorMessage = "Error al listar los usuarios debido a un problema en la base de datos: ";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
        }

    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) throws ResourceNotFoundException {
        try {
            UserDTO user = authService.findUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        ResponseEntity<String> response = null;
        authService.deleteUser(id);
        response = ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado con éxito");
        return response;
    }
}
