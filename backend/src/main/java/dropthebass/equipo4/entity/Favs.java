package dropthebass.equipo4.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "favoritos")
@Getter
@NoArgsConstructor
public class Favs {
    @Id
    @SequenceGenerator(name = "favs_sequence", sequenceName = "favs_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favs_sequence")
    private Long id;

    @ManyToMany(targetEntity = Product.class, fetch = FetchType.LAZY) // Se quita mappedBy y "products" debe coincidir con el nombre del atributo en la clase Product
    @JoinTable(
            name = "favs_products",
            joinColumns = @JoinColumn(name = "favs_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Favs(Set<Product> products, User user) {
        this.products = products;
        this.user = user;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
