package dropthebass.equipo4.dto;

import dropthebass.equipo4.entity.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    private String email;
    private String name;
    private String lastname;
    private String password;
    private ERole role;


}
