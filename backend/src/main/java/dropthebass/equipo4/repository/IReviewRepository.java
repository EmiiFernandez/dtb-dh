package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Product;
import dropthebass.equipo4.entity.Review;
import dropthebass.equipo4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.user.email = :userEmail AND r.product.id = :productId")
    List<Review> findByUserEmailAndProductId(String userEmail, Long productId);

    @Query("SELECT COALESCE(AVG(r.productScoring), 0.0) FROM Review r WHERE r.product.id = :productId")
    double calculateAverageProductScoring(Long productId);
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId")
    List<Review> findReviewsByProductId(@Param("productId") Long productId);
    boolean existsByUserIdAndProductId(Long user_id, Long product_id);


}
