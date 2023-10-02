package dropthebass.equipo4.repository;

import dropthebass.equipo4.entity.DetailBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface IDetailBookingRepository extends JpaRepository<DetailBooking, Long> {
    List<DetailBooking> findByBookingId (Long bookingId);

    List<DetailBooking> findByUserEmail(String userEmail);
    @Query("SELECT db FROM DetailBooking db WHERE db.booking.user.email = :userEmail")
    List<DetailBooking> findDetailBookingsByUserEmail(@Param("userEmail") String userEmail);
    @Query("SELECT COUNT(db) > 0 FROM DetailBooking db WHERE db.user.id = :userId AND db.productId = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

   //lista de objetos con las fechas de inicio y fin de reserva
   @Query("SELECT db.startBooking, db.endBooking " +
           "FROM DetailBooking db " +
           "WHERE db.productId = :productId")
   List<Object[]> findOccupiedDatesByProductId(@Param("productId") Long productId);

//busca las fechas ocupadas de un productos
    @Query("SELECT db FROM DetailBooking db WHERE db.productId = :productId " +
            "AND db.startBooking <= :endDate AND db.endBooking >= :startDate")
    List<DetailBooking> findOccupiedBookings(@Param("productId") Long productId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);


}
