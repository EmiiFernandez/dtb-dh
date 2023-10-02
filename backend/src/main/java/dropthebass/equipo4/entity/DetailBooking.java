package dropthebass.equipo4.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@ToString
@NoArgsConstructor
@Getter
@Table(name = "detalles_reserva")
public class DetailBooking {
    @Id
    @SequenceGenerator(name = "cart_sequence", sequenceName = "cart_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_sequence")
    private Long id;

    @Column(name = ("id_producto"))
    private Long productId;

    @Column(name = "nombre_producto")
    private String productName;

    @Column(name = "precio_producto")
    private Double productPrice;

    @Column(name = "marca_producto")
    private String productBrand;

    @ManyToOne (optional = false, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Booking booking;

    @Column(name="cantidad")
    private int amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_inicio_reserva")
    private Date startBooking;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_fin_reserva")
    private Date endBooking;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_reserva")
    private Date dateBooking;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public DetailBooking(Long productId, String productName, Double productPrice, String productBrand, Booking booking, int amount, Date startBooking, Date endBooking, Date dateBooking, User user) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productBrand = productBrand;
        this.booking = booking;
        this.amount = amount;
        this.startBooking = startBooking;
        this.endBooking = endBooking;
        this.dateBooking = dateBooking;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDateBooking(Date dateBooking) {
        this.dateBooking = dateBooking;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public void setStartBooking(Date startBooking) {
        this.startBooking = startBooking;
    }

    public void setEndBooking(Date endBooking) {
        this.endBooking = endBooking;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
