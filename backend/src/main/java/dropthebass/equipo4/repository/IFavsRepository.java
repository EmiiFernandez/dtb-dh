package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Favs;
import dropthebass.equipo4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IFavsRepository extends JpaRepository<Favs, Long> {

    @Query("SELECT f FROM Favs f WHERE f.user = :user")
    Favs findFavsByUser(@Param("user") User user);

    @Query("SELECT f FROM Favs f JOIN FETCH f.products WHERE f.user = :user")
    Favs findFavsxProductsByUser(@Param("user") User user);

}
