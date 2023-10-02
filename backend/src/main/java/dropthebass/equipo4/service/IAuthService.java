package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.SignupRequest;
import dropthebass.equipo4.dto.UserDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IAuthService {

    boolean createUser(SignupRequest signupRequest);

    public boolean promoteToAdmin(String emailUser);
    public boolean changeToUser(String emailUser);

    public void deleteUser(Long id);

    public UserDTO findUserByEmail (String email) throws ResourceNotFoundException;

    public List<UserDTO> listUsers();
}
