package dropthebass.equipo4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BrandDTO {
    private Long id;
    private String name;
    private String country;
    private String webSite;
    private Set<ProductDTO> products;
    private String brandImageId;


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

    public void setProducts(Set<ProductDTO> products) {
        this.products = products;
    }
}
