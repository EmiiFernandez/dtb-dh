package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT category FROM Category category WHERE category.id = :id")
    Optional<Category> findById(@Param("id") Long id);

    Category findByName(String name);

}
