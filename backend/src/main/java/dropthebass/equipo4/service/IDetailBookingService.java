package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.DetailBookingDTO;
import dropthebass.equipo4.dto.OccupiedDateDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IDetailBookingService {
    public void createDetail(DetailBookingDTO detailBookingDTO);
    public List<DetailBookingDTO> getDetailByBooking(Long bookingId) throws ResourceNotFoundException;

    public List<DetailBookingDTO> getDetailsByUserEmail() throws ResourceNotFoundException;

    public List<OccupiedDateDTO> getOccupiedDatesByProductId(Long productId);

    public boolean isProductAvailableBetweenDates(Long productId, OccupiedDateDTO occupiedDateDTO);

}
