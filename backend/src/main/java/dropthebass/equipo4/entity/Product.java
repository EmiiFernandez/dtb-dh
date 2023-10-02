package dropthebass.equipo4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Entity
@ToString
@Getter
@NoArgsConstructor
@Table(name = "productos")
public class Product {

    @Id
    @SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence")
    private Long id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "precio")
    @Digits(integer = 10, fraction = 2)
    @DecimalMax(value = "9999999.99")
    private Double price;

    @Column(name = "stock")
    private Integer stock;

    @ManyToOne(targetEntity = Brand.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "marca_id")
    @JsonIgnoreProperties("products")
    private Brand brand;

    @ManyToOne(targetEntity = Category.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria_id")
    @JsonIgnore
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_feature",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    @JsonIgnore
    private Set<Feature> features = new HashSet<>();

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Favs> favs = new HashSet<>();

    @OneToMany(targetEntity = Review.class, mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Review> reviews;

    public Product(String name, String description, Double price, Integer stock, Brand brand, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public void setFavs(Set<Favs> favs) {
        this.favs = favs;
    }

    public void addFeature(Feature feature) {
        this.features.add(feature);
        feature.getProducts().add(this);
    }

    public void removeFeature (Long featureId) {
        Optional<Feature> optionalFeature = this.features.stream().filter(c -> c.getId().equals(featureId)).findFirst();
        if (optionalFeature.isPresent()) {
            Feature feature = optionalFeature.get();
            this.features.remove(feature);
            feature.getProducts().remove(this);
        }
    }

    public void addFavs(Favs favs) {
        this.favs.add(favs);
    }

    public void removeFavs(Favs favs) {
        this.favs.remove(favs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}