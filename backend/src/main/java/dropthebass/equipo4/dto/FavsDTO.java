package dropthebass.equipo4.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavsDTO {
    private Long id;
    private Set<ProductDTO> products;
    private UserDTO user;

    public void setProducts(Set<ProductDTO> products) {
        this.products = products;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
