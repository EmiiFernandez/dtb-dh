package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.SignupRequest;
import dropthebass.equipo4.dto.UserDTO;
import dropthebass.equipo4.entity.User;
import dropthebass.equipo4.entity.enums.ERole;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.IAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    ObjectMapper mapper;

    @Autowired
    public AuthServiceImpl(IUserRepository userRepository, PasswordEncoder passwordEncoder, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public boolean createUser(SignupRequest signupRequest) {
        try {
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                return false;
            }

            User user = new User();
            BeanUtils.copyProperties(signupRequest, user);

            String hashPassWord = passwordEncoder.encode(signupRequest.getPassword());
            user.setPassword(hashPassWord);
            if (signupRequest.getRole() == null) {
                user.setRole(ERole.ROLE_USER);
            } else {
                user.setRole(signupRequest.getRole());
            }

            userRepository.save(user);

            return true;
        } catch (DataAccessException e) {
            throw new DataAccessException("Error al crear el usuario en la base de datos" + e.getMessage()) {
            };
        }
    }

    @Override
    public boolean promoteToAdmin(String emailUser) {
        // Obtener el usuario autenticado desde Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName(); // Obtiene el correo electrónico del usuario autenticado

        Optional<User> optionalAdmin = userRepository.findByEmail(adminEmail);
        Optional<User> optionalUser = userRepository.findByEmail(emailUser);

        if (optionalAdmin.isPresent() && optionalUser.isPresent()) {
            User admin = optionalAdmin.get();
            User user = optionalUser.get();

            if (admin.getRole() == ERole.ROLE_ADMIN || admin.getRole() == ERole.ROLE_DEV) {
                user.setRole(ERole.ROLE_ADMIN);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean changeToUser(String emailUser) {
        // Obtener el usuario autenticado desde Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String adminEmail = authentication.getName(); // Obtiene el correo electrónico del usuario autenticado

        Optional<User> optionalAdmin = userRepository.findByEmail(adminEmail);
        Optional<User> optionalUser = userRepository.findByEmail(emailUser);

        if (optionalAdmin.isPresent() && optionalUser.isPresent()) {
            User admin = optionalAdmin.get();
            User user = optionalUser.get();

            if (admin.getRole() == ERole.ROLE_ADMIN || admin.getRole() == ERole.ROLE_DEV) {
                user.setRole(ERole.ROLE_USER);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }


    @Override
    public void deleteUser(Long id) {

        // Realizar la eliminación en la base de datos
        userRepository.deleteById(id);

        // Invalidar la sesión si el usuario eliminado está conectado
        SecurityContextHolder.clearContext();
    }
    @Override
    public UserDTO findUserByEmail(String email) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return mapper.convertValue(user.get(), UserDTO.class);
        } else {
            throw new ResourceNotFoundException("Usuario no encontrado para el email: " + email);
        }
    }

    @Override
    public List<UserDTO> listUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOS = new ArrayList<>();

            for (User user : users) {

                UserDTO userDTO = mapper.convertValue(user, UserDTO.class);
                userDTOS.add(userDTO);
            }

            return userDTOS;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al intentar listar los usuarios");
        }
    }

}