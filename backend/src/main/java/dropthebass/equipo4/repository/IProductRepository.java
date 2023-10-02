package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Brand;
import dropthebass.equipo4.entity.Category;
import dropthebass.equipo4.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT product FROM Product product WHERE product.id = :id")
    Optional<Product> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.name = :productName AND p.brand = :brand")
    boolean existsByNameAndBrand(@Param("productName") String productName, @Param("brand") Brand brand);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.name = :productName AND p.category = :category")
    boolean existsByNameAndCategory(@Param("productName") String productName, @Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE (p.name = :name AND p.brand.id = :brand_id) OR (p.name = :name AND p.category.id = :category_id)")
    List<Product> findByNameAndBrandOrNameAndCategory(@Param("name") String name, @Param("brand_id") Long brandId, @Param("category_id") Long categoryId);

    @Query("SELECT p FROM Product p JOIN p.features c WHERE c.id = :feature_id")
    List<Product> findProductsByFeatureId(@Param("feature_id") Long featureId);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.features " +
            "LEFT JOIN FETCH p.category " +
            "WHERE p.id = :productId")
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);


}
