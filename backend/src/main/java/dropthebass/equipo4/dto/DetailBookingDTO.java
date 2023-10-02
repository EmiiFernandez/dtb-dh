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
public class DetailBookingDTO {
    private int amount;
    private Long productId;
    private String productName;
    private Double productPrice;
    private String productBrand;
    private BookingDTO booking;
    private Date dateBooking;


}
