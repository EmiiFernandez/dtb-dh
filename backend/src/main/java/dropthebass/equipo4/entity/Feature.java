package dropthebass.equipo4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Table(name = "features")
@Getter
@NoArgsConstructor
public class Feature {
    @Id
    @SequenceGenerator(name = "feature_sequence", sequenceName = "feature_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feature_sequence")
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(unique = true)
    private String icon;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "features")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    public Feature(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
        product.getFeatures().add(this);
    }
}
