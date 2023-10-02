package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Feature;
import dropthebass.equipo4.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFeatureRepository extends JpaRepository<Feature, Long> {
    @Query("SELECT feature FROM Feature feature WHERE feature.id = :id")
    Optional<Feature> findFeatureById(@Param("id") Long id);
    @Query("SELECT p FROM Product p JOIN p.features f WHERE f.id = :feature_id")
    List<Product> findProductsByFeatureId(@Param("feature_id") Long featureId);
    @Query("SELECT feature FROM Feature feature WHERE feature.name = :name")
    Feature findByName(@Param("name") String name);}
