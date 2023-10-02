package dropthebass.equipo4.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorias")
@Getter
@NoArgsConstructor
public class Category {
    @Id
    @SequenceGenerator(name = "category_sequence", sequenceName = "category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_sequence")
    private Long id;
    @Column(name = "nombre", unique = true)
    private String name;

    //PERSIST: Al eliminar el producto no afectará la categoría
    @OneToMany(targetEntity = Product.class, mappedBy = "category", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Product> products;

    public Category(String name, Set<Product> products) {
        this.name = name;
        this.products = products;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        if(products == null) {
            products = new HashSet<>();
        }
        products.add(product);
        product.setCategory(this);
    }
    public void removeProduct(Product product) {
        this.getProducts().remove(product);
        product.setCategory(this);
    }
}
