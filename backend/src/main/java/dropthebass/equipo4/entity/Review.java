package dropthebass.equipo4.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="resenas")
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @SequenceGenerator(name = "review_sequence", sequenceName = "review_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_sequence")
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name="fecha_resena")
    private Date reviewDate;

    @Column(name="puntaje_producto")
    private Integer productScoring;

    @Column(name="comentarios")
    private String comments;

    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;


    public Review(Integer productScoring, String comments) {
        this.productScoring = productScoring;
        this.comments = comments;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
    public void setProductScoring(Integer productScoring) {
        this.productScoring = productScoring;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
