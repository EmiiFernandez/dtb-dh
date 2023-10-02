package dropthebass.equipo4.dto;

import dropthebass.equipo4.entity.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
  //  private String refreshToken;
    private String jwtToken;
    private String name;
    private String lastname;
  private String email;
  private ERole role;
  private String firstLetterNameAndLastname;
}
