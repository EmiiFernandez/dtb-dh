package dropthebass.equipo4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private String bookingCode;
    private Date dateBooking;
    private Double totalCost;
    private UserDTO user;
}
