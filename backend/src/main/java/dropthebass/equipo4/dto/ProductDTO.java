package dropthebass.equipo4.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private BrandDTO brand;
    private CategoryDTO category;
    private Set<FeatureDTO> features;

    public ProductDTO(String name, String description, Double price, Integer stock, BrandDTO brand, CategoryDTO category, Set<FeatureDTO> features) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
        this.features = features;
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

    public void setBrand(BrandDTO brand) {
        this.brand = brand;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public void setFeatures(Set<FeatureDTO> features) {
        this.features = features;
    }

}
