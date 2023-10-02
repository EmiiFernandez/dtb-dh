package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.ReviewDTO;
import dropthebass.equipo4.exeptions.BadRequestException;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin
public class ReviewController {
    @Autowired
    private IReviewService reviewService;


    @PostMapping("/{productId}")
    public ResponseEntity<Object> createReview(
            @PathVariable Long productId,
            @RequestBody ReviewDTO reviewDTO
    ) {
        try {
            // Obtiene la información de autenticación actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Verifica si la autenticación contiene detalles del usuario y es una instancia de UserDetails
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                String username = userDetails.getUsername();

                ReviewDTO createdReview = reviewService.createReview(username, productId, reviewDTO);

                return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
            } else {
                // Si no hay detalles de usuario en la autenticación, retorna una respuesta de error no autorizado
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            // En caso de cualquier otra excepción, se maneja como una respuesta de error interno del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor" + e.getMessage());
        }
    }
        @GetMapping()
    public ResponseEntity<List<ReviewDTO>> listAllBookings() {
        List<ReviewDTO> bookings = reviewService.listReviews();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

   /* @PostMapping("/{bookingId}/{productId}")
    public ResponseEntity<?> createReviewForProductInBooking(
            @PathVariable Long bookingId,
            @PathVariable Long productId,
            @RequestBody ReviewDTO reviewDTO
    ) {
        try {
            ReviewDTO createdReviewDTO = reviewService.createReviewForProductInBooking(bookingId, productId, reviewDTO);
            return ResponseEntity.ok(createdReviewDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
*/
    @GetMapping("/avg/{productId}")
    public ResponseEntity<Double> getAverageProductScoring(@PathVariable Long productId) throws ResourceNotFoundException {
        Double averageScoring = reviewService.calculateAverageProductScoring(productId);
        return ResponseEntity.ok(averageScoring);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) throws ResourceNotFoundException {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
