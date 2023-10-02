package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.BookingDTO;
import dropthebass.equipo4.dto.OccupiedDateDTO;
import dropthebass.equipo4.dto.ShoppingCartDTO;
import dropthebass.equipo4.exeptions.BadRequestException;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IBookingService {
    BookingDTO createBooking() throws DuplicateException, DatabaseException, BadRequestException, ResourceNotFoundException;
    public List<BookingDTO> getBookingByUser(String userEmail);
}
