package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.SignupRequest;
import dropthebass.equipo4.dto.UserDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IAuthService;
import dropthebass.equipo4.service.impl.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final IAuthService authService;
    private final MailService mailService;

    @Autowired
    public SignupController(IAuthService authService, MailService mailService) {
        this.authService = authService;
        this.mailService = mailService;
    }

    /**
     * Registra un nuevo usuario y envía un correo de confirmación.
     *
     * @param signupRequest Los detalles del usuario a registrar.
     * @return ResponseEntity con el estado de la operación.
     */
    @PostMapping
    public ResponseEntity<String> signupUser(@RequestBody SignupRequest signupRequest) throws MessagingException, IOException {
        try {
            boolean isUserCreated = authService.createUser(signupRequest);
            if (isUserCreated) {
                mailService.sendMessageUser(signupRequest.getEmail(), signupRequest.getName());
                return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con éxito. Se envió confirmación por correo electrónico.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario con el email " + signupRequest.getEmail() + " ya existe");
            }
        } catch (DataAccessException e) {
            // Error específico de creación de usuarios (por ejemplo, problema con la base de datos)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario: " + e.getMessage());
        }
    }

    /**
     * Reenvía el correo de confirmación para un usuario.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @return ResponseEntity con el estado de la operación.
     */
    @PostMapping("/resend-email")
    public ResponseEntity<String> resendConfirmationEmail(@RequestBody String email) throws MessagingException, IOException, ResourceNotFoundException {
        try {
            // Buscar al usuario por su correo electrónico
            UserDTO userDTO = authService.findUserByEmail(email);

            // Reenviar el correo de confirmación utilizando sendMessageUser
            mailService.sendMessageUser(userDTO.getEmail(), userDTO.getName());

            // Retornar una respuesta exitosa
            return ResponseEntity.status(HttpStatus.OK).body("Correo de confirmación enviado exitosamente.");
        } catch (ResourceNotFoundException e) {
            // Manejar el caso en que no se encuentra el usuario
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún usuario con el correo electrónico ingresado.");
        } catch (Exception e) {
            // Manejar otros errores que puedan ocurrir al enviar el correo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar enviar el correo de confirmación.");
        }
    }
}