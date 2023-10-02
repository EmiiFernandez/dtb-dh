package dropthebass.equipo4.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="reservas")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Booking {
    @Id
    @SequenceGenerator(name = "booking_sequence", sequenceName = "booking_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_sequence")
    private Long id;

    @Column(name = "codigo_reserva", unique = true)
    private String bookingCode;

    @Column (name="costo_total")
    private Double totalCost;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_reserva")
    private Date dateBooking;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    public Booking(String bookingCode, Double totalCost, Date dateBooking, User user) {
        this.bookingCode = bookingCode;
        this.totalCost = totalCost;
        this.dateBooking = dateBooking;
        this.user = user;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public void setDateBooking(Date dateBooking) { this.dateBooking = dateBooking; }

    public void setUser(User user) {
        this.user = user;
    }
}
