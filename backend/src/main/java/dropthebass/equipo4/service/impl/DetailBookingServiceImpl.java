package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.DetailBookingDTO;
import dropthebass.equipo4.dto.OccupiedDateDTO;
import dropthebass.equipo4.entity.DetailBooking;
import dropthebass.equipo4.entity.User;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IDetailBookingRepository;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.IDetailBookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DetailBookingServiceImpl implements IDetailBookingService {

    private IDetailBookingRepository detailBookingRepository;

    private ObjectMapper mapper;

    private HttpSession session;

    private IUserRepository userRepository;


    public DetailBookingServiceImpl(IDetailBookingRepository detailBookingRepository, ObjectMapper mapper, HttpSession session, IUserRepository userRepository) {
        this.detailBookingRepository = detailBookingRepository;
        this.mapper = mapper;
        this.session = session;
        this.userRepository = userRepository;
    }

    @Override
    public void createDetail(DetailBookingDTO detailBookingDTO) {
        DetailBooking detailBooking = mapper.convertValue(detailBookingDTO, DetailBooking.class);

        this.detailBookingRepository.save(detailBooking);
    }

    public List<DetailBookingDTO> getDetailByBooking(Long bookingId) throws ResourceNotFoundException {
        List<DetailBooking> detailBookingList = detailBookingRepository.findByBookingId(bookingId);

        if (detailBookingList.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron detalles de reserva para la reserva con ID: " + bookingId);
        }

        List<DetailBookingDTO> detailBookingDTOList = detailBookingList.stream()
                .map(entity -> mapper.convertValue(entity, DetailBookingDTO.class))
                .collect(Collectors.toList());
        return detailBookingDTOList;
    }

    @Override
    public List<DetailBookingDTO> getDetailsByUserEmail() throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión
        User authenticatedUser = getAuthenticatedUser(session);

        // Cargar una instancia gestionada del usuario desde la base de datos
        authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Usar el método personalizado para buscar detalles de reserva por correo electrónico del usuario
        List<DetailBooking> detailBookingList = detailBookingRepository.findDetailBookingsByUserEmail(authenticatedUser.getEmail());

        if (detailBookingList.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron detalles de reserva para el usuario con correo electrónico: " + authenticatedUser.getEmail());
        }

        // Convertir las entidades DetailBooking a DTOs (DetailBookingDTO) y devolverlas en una lista
        List<DetailBookingDTO> detailBookingDTOList = detailBookingList.stream()
                .map(entity -> mapper.convertValue(entity, DetailBookingDTO.class))
                .collect(Collectors.toList());

        return detailBookingDTOList;
    }

    private User getAuthenticatedUser(HttpSession session) throws ResourceNotFoundException {
        User userLogged = (User) session.getAttribute("userSession");
        if (userLogged == null) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }
        return userLogged;
    }

    //Lisat de fechas ocupadas
    @Override
    public List<OccupiedDateDTO> getOccupiedDatesByProductId(Long productId) {
        List<Object[]> dateArrays = detailBookingRepository.findOccupiedDatesByProductId(productId);

        List<OccupiedDateDTO> occupiedDates = new ArrayList<>();
        for (Object[] dateArray : dateArrays) {
            Date startDate = (Date) dateArray[0];
            Date endDate = (Date) dateArray[1];
            occupiedDates.add(new OccupiedDateDTO(startDate, endDate));
        }

        return occupiedDates;
    }

    //Chequea si esta ocupada
    public boolean isProductAvailableBetweenDates(Long productId, OccupiedDateDTO occupiedDateDTO) {
    Date startDate = occupiedDateDTO.getStartDate();
    Date endDate = occupiedDateDTO.getEndDate();

    List<DetailBooking> occupiedBookings = detailBookingRepository.findOccupiedBookings(productId, startDate, endDate);
    return occupiedBookings.isEmpty(); // true si las fechas están disponibles, false si están ocupadas
}
}