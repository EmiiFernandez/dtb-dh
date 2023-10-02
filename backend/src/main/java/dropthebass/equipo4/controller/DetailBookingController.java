package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.DetailBookingDTO;
import dropthebass.equipo4.dto.OccupiedDateDTO;
import dropthebass.equipo4.entity.DetailBooking;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IDetailBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/detail-booking")
public class DetailBookingController {
    private IDetailBookingService detailBookingService;

    @Autowired
    public DetailBookingController(IDetailBookingService detailBookingService) {
        this.detailBookingService = detailBookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<DetailBookingDTO>> getDetailsByBooking(@PathVariable("id") Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(detailBookingService.getDetailByBooking(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<DetailBookingDTO>> getDetailsByAuthenticatedUser() {
        try {
            List<DetailBookingDTO> detailBookings = detailBookingService.getDetailsByUserEmail();
            return new ResponseEntity<>(detailBookings, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Boolean: true --> fechas disponibles
    //         false --> fechas ocupadas
    @PostMapping("/check-availability/{productId}")
    public ResponseEntity<Boolean> checkProductAvailability(
            @PathVariable("productId") Long productId,
            @RequestBody OccupiedDateDTO occupiedDateDTO) {

        boolean isAvailable = detailBookingService.isProductAvailableBetweenDates(productId, occupiedDateDTO);

        HttpStatus responseStatus = isAvailable ? HttpStatus.OK : HttpStatus.CONFLICT;
        return new ResponseEntity<>(isAvailable, responseStatus);
    }

    @GetMapping("/occupied-dates")
    public List<OccupiedDateDTO> getOccupiedDatesByProductId(@RequestParam("productId") Long productId) {
        return detailBookingService.getOccupiedDatesByProductId(productId);
    }

}