package dropthebass.equipo4.service.jwt;

import dropthebass.equipo4.entity.User;
import dropthebass.equipo4.repository.IUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Este método se llama cuando un usuario intenta iniciar sesión.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Paso 1: Buscar al usuario por su dirección de correo electrónico en el repositorio de usuarios.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no registrado con el email: " + email));

        // Paso 2: Crear una colección de autoridades (roles) para el usuario.
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        // Paso 3: Obtener la sesión HTTP actual a través de Spring RequestContextHolder.
        //Nos permite utilizar los datos del usuario logueado donde necesitemos verificar
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attributes.getRequest().getSession(true);

        // Paso 4: Almacenar la información del usuario en la sesión con una clave "userSession".
        session.setAttribute("userSession", user);
        session.setAttribute("userEmail", email);


        // Paso 5: Crear y devolver un objeto UserDetails que representa al usuario autenticado.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Nombre de usuario (correo electrónico)
                user.getPassword(), // Contraseña
                authorities // Colección de roles/autoridades
        );
    }
}
