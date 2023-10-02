package dropthebass.equipo4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Table(name = "marcas")
@NoArgsConstructor
@Getter
public class Brand {
    @Id
    @SequenceGenerator(name = "brand_sequence", sequenceName = "brand_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_sequence")
    private Long id;

    @NotBlank
    @Column(name = "nombre")
    private String name;

    @Column(name = "pais")
    private String country;

    @Column(name = "web_site")
    private String webSite;

    //PERSIST: Al eliminar el producto no afectará la marca
    @OneToMany(targetEntity = Product.class, mappedBy = "brand", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Product> products;

    @Column(unique = true)
    private String brandImageId;


    public Brand(String name, String country, String webSite, Set<Product> products, String brandImageId) {
        this.name = name;
        this.country = country;
        this.webSite = webSite;
        this.products = products;
        this.brandImageId = brandImageId;
    }

    public void setBrandImageId(String brandImageId) {
        this.brandImageId = brandImageId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        if(products == null) {
            products = new HashSet<>();
        }
        products.add(product);
        product.setBrand(this);
    }
    public void removeProduct(Product product) {
        this.getProducts().remove(product);
        product.setBrand(this);
    }
}
