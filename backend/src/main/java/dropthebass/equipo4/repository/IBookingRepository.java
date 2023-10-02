package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
  boolean existsByBookingCode(String bookingCode);

  List<Booking> findByUserEmail(String email);

  //  @Query("SELECT b FROM Booking b WHERE b.user = :user")
 //   List<Booking> findAllBookingsByUser(@Param("user") User user);

}
