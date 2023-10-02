package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.LoginRequest;
import dropthebass.equipo4.dto.LoginResponse;
import dropthebass.equipo4.entity.User;
import dropthebass.equipo4.entity.enums.ERole;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.jwt.UserServiceImpl;
import dropthebass.equipo4.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    //private RefreshTokenService refreshTokenService;

    private final UserServiceImpl userService;

    private IUserRepository userRepository;


    private  final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserServiceImpl userService, IUserRepository userRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String name = user.get().getName();
        String lastname = user.get().getLastname();
        String email = user.get().getEmail();

        // Obtener el rol del usuario y asignarlo
        ERole role = user.get().getRole();

        String firstLetterName = name.substring(0,1).toUpperCase();
        String firstLetterLastName = lastname.substring(0,1).toUpperCase();

        String firstLetterNameAndLastname = firstLetterName + firstLetterLastName;

        String jwt = jwtUtil.generateToken(userDetails.getUsername());



        LoginResponse loginResponse = new LoginResponse(jwt, name, lastname, email, role, firstLetterNameAndLastname);

        return ResponseEntity.ok(loginResponse);

    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidar la sesión y eliminar el token de acceso
        SecurityContextHolder.getContext().setAuthentication(null);
        request.getSession().invalidate();

        // Redirigir a la página de inicio de sesión
        return "redirect:/auth/login";
    }
}
/*

        Cookie jwtCookie = new Cookie("jwtToken", jwt);
        jwtCookie.setMaxAge(30 * 60); // Tiempo de expiración de la cookie en segundos
        jwtCookie.setDomain(null); // Establecer el dominio en null
        jwtCookie.setPath("/"); // Establecer el camino en "/"
        jwtCookie.setSecure(false); // Establecer como no segura (para HTTP)


        response.addCookie(jwtCookie);

 */

/*    @PostMapping("/refreshToken")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtil.generateToken(user.getEmail());
                    return LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }
*/