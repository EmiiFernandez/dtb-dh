package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.BookingDTO;
import dropthebass.equipo4.entity.DetailBooking;
import dropthebass.equipo4.exeptions.BadRequestException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IDetailBookingRepository;
import dropthebass.equipo4.service.IBookingService;
import dropthebass.equipo4.service.impl.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private IBookingService bookingService;

    private IDetailBookingRepository detailBookingRepository;


    private final MailService mailService;
    @Autowired
    public BookingController(IBookingService bookingService, IDetailBookingRepository detailBookingRepository, MailService mailService) {
        this.bookingService = bookingService;
        this.detailBookingRepository = detailBookingRepository;
        this.mailService = mailService;
    }


    @PostMapping()
    public ResponseEntity<?> createBooking() throws BadRequestException, ResourceNotFoundException {
        try {
            // Intentar crear la reserva
            BookingDTO newBooking = bookingService.createBooking();

            // Obtener los detalles de reserva para la nueva reserva
            List<DetailBooking> detailBookings = detailBookingRepository.findByBookingId(newBooking.getId());

            // Después de crear la reserva, envía un correo de confirmación con los detalles de reserva
            mailService.sendMessageBooking(newBooking.getUser().getEmail(), detailBookings);

            return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
        } catch (DuplicateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo crear la reserva." + e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
