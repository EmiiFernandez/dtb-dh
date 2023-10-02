package dropthebass.equipo4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private Set<ProductDTO> products;

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(Set<ProductDTO> products) {
        this.products = products;
    }
}
