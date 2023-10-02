package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
    Long countByUserEmail(String userEmail);


}