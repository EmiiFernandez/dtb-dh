package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT brand FROM Brand brand WHERE brand.id = :id")
    Optional<Brand> findBrandById(@Param("id") Long id);
    Brand findByName(String name);

    boolean existsByName(String name);

}