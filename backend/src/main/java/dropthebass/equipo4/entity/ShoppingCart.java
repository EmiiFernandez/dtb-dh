package dropthebass.equipo4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.util.Date;

@Entity
@ToString
@NoArgsConstructor
@Getter
@Table(name = "carrito_reservas")
public class ShoppingCart {
    @Id
    @SequenceGenerator(name = "shoopingcart_sequence", sequenceName = "shoopingcart_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shoopingcart_sequence")
    private Long id;

    @Column(name = "cantidad")
    private int amount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_inicio_reserva")
    private Date startBooking;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "fecha_fin_reserva")
    private Date endBooking;

    @ManyToOne(optional = false, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Product product;

    @ManyToOne(optional = false, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private User user;

    public ShoppingCart(int amount, Date startBooking, Date endBooking, Product product, User user) {
        this.amount = amount;
        this.startBooking = startBooking;
        this.endBooking = endBooking;
        this.product = product;
        this.user = user;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStartBooking(Date startBooking) {
        this.startBooking = startBooking;
    }

    public void setEndBooking(Date endBooking) {
        this.endBooking = endBooking;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setUser(User user) {
        this.user = user;
    }
}