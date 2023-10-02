package dropthebass.equipo4.dto;

import dropthebass.equipo4.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeatureDTO {
    private Long id;
    private String name;

    private String icon;
   private Set<Product> products;

    public void setName(String name) {
        this.name = name;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
