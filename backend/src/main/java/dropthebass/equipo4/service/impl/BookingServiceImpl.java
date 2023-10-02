package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.BookingDTO;
import dropthebass.equipo4.dto.OccupiedDateDTO;
import dropthebass.equipo4.dto.ShoppingCartDTO;
import dropthebass.equipo4.entity.*;
import dropthebass.equipo4.exeptions.*;
import dropthebass.equipo4.repository.IBookingRepository;
import dropthebass.equipo4.repository.IDetailBookingRepository;
import dropthebass.equipo4.repository.IProductRepository;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.IBookingService;
import dropthebass.equipo4.utils.BookingCodeGenerator;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements IBookingService {

    private IBookingRepository bookingRepository;
    private ShoppingCartServiceImpl shoppingCartService;

    private IUserRepository userRepository;

    private IDetailBookingRepository detailBookingRepository;

    private IProductRepository productRepository;
    private BookingCodeGenerator bookingCodeGenerator;

    private HttpSession session;
    ObjectMapper mapper;


    @Autowired
    public BookingServiceImpl(IBookingRepository bookingRepository, ShoppingCartServiceImpl shoppingCartService, IUserRepository userRepository, IDetailBookingRepository detailBookingRepository, IProductRepository productRepository, BookingCodeGenerator bookingCodeGenerator, HttpSession session, ObjectMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.shoppingCartService = shoppingCartService;
        this.userRepository = userRepository;
        this.detailBookingRepository = detailBookingRepository;
        this.productRepository = productRepository;
        this.bookingCodeGenerator = bookingCodeGenerator;
        this.session = session;
        this.mapper = mapper;
    }

    @Override
    public List<BookingDTO> getBookingByUser(String userEmail) {
        List<Booking> bookingList = bookingRepository.findByUserEmail(userEmail);

        List<BookingDTO> bookingDTOList = bookingList.stream()
                .map(entity -> mapper.convertValue(entity, BookingDTO.class ))
                .collect(Collectors.toList());

        return bookingDTOList;
    }

    @Override
    public BookingDTO createBooking() throws DuplicateException, DatabaseException, BadRequestException, ResourceNotFoundException {
        try {
            // Obtener el usuario autenticado de la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Cargar una instancia gestionada del usuario desde la base de datos
            authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear una instancia de la entidad Booking
            Booking booking = new Booking();
            booking.setUser(authenticatedUser);

            // Generar un código de reserva único
            String reservationCode;
            do {
                reservationCode = bookingCodeGenerator.generateUniqueReservationCode();
            } while (bookingRepository.existsByBookingCode(reservationCode));

            // Asignar el código de reserva generado
            booking.setBookingCode(reservationCode);

            // Obtener la fecha actual
            ZoneId timeZone = ZoneId.of("America/Argentina/Buenos_Aires");
            Instant currentInstant = Instant.now();
            Date currentDate = Date.from(currentInstant.atZone(timeZone).toInstant());

            booking.setDateBooking(currentDate);

            // Obtener el carrito de compras del usuario autenticado
            List<ShoppingCartDTO> shoppingCartDTOList = shoppingCartService.getListByUser();

            // Procesar cada elemento del carrito y crear detalles de reserva
            List<DetailBooking> detailBookings = new ArrayList<>();
            double totalCost = 0.0;

            for (ShoppingCartDTO shoppingCartItem : shoppingCartDTOList) {
                DetailBooking detailBooking = new DetailBooking();
                Product product = productRepository.findById(shoppingCartItem.getProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + shoppingCartItem.getProduct().getId()));

                // Días de reserva para el producto
                int bookingDays = calculateBookingDays(shoppingCartItem.getStartBooking(), shoppingCartItem.getEndBooking());

                // Configurar los detalles de reserva según los datos del carrito de compras y el DTO de reserva
                detailBooking.setProductId(product.getId());
                detailBooking.setProductName(product.getName());
                detailBooking.setProductPrice(product.getPrice());
                detailBooking.setProductBrand(product.getBrand().getName());
                detailBooking.setAmount(shoppingCartItem.getAmount());
                detailBooking.setStartBooking(shoppingCartItem.getStartBooking());
                detailBooking.setEndBooking(shoppingCartItem.getEndBooking());
                detailBooking.setUser(authenticatedUser);
                detailBooking.setDateBooking(booking.getDateBooking());
                // Calcula el costo para este detalle de reserva
                double productCost = product.getPrice() * bookingDays;
                totalCost += productCost;

                // Agregar el detalle de reserva a la lista
                detailBooking.setBooking(booking);
                detailBookings.add(detailBooking);
            }

            // Asignar el costo total de la reserva
            booking.setTotalCost(totalCost);

            // Guardar la reserva en la base de datos
            booking = bookingRepository.save(booking);

            // Guardar los detalles de reserva en la base de datos
            detailBookingRepository.saveAll(detailBookings);

            // Limpiar el carrito de compras después de crear la reserva.
            shoppingCartService.cleanShoppingCart(authenticatedUser.getEmail());

            // Convierte la reserva guardada en un DTO y devuélvela
            BookingDTO savedBookingDTO = mapper.convertValue(booking, BookingDTO.class);
            return savedBookingDTO;
        } catch (Exception e) {
            throw new DatabaseException("Error al crear la reserva: " + e.getMessage());
        }
    }

    // Calcular el número de días de reserva
    public Integer calculateBookingDays(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            // Argumento inválido
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }

        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();

        LocalDate startLocalDate = startInstant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        if (startLocalDate.isAfter(endLocalDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        long daysBetween = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);

        // Math.toIntExact se utiliza para convertir el resultado del cálculo de días (un long) en un int
        return Math.toIntExact(daysBetween) + 1; // Sumar 1 para incluir el día de inicio
    }

    public Double calculateTotalCost(List<ShoppingCartDTO> shoppingCartDTOList) {
        if (shoppingCartDTOList == null || shoppingCartDTOList.isEmpty()) {
            return 0.0; // No hay elementos en el carrito, el costo total es 0.
        }

        Double totalCost = 0.0;

        for (ShoppingCartDTO shoppingCartItem : shoppingCartDTOList) {
            if (shoppingCartItem == null || shoppingCartItem.getProduct() == null || shoppingCartItem.getProduct().getPrice() == null) {
                throw new IllegalArgumentException("Los productos y los precios no pueden ser nulos.");
            }

            // Calcula el costo total para cada producto en el carrito
            totalCost += shoppingCartItem.getProduct().getPrice() * calculateBookingDays(shoppingCartItem.getStartBooking(), shoppingCartItem.getEndBooking());
        }

        return totalCost;
    }

    private User getAuthenticatedUser(HttpSession session) throws ResourceNotFoundException {
        User userLogged = (User) session.getAttribute("userSession");
        if (userLogged == null) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }
        return userLogged;
    }


/*
    @Override
    public BookingDTO findBookingId(Long id) throws ResourceNotFoundException {
        try {
            // Buscar la reserva por su ID en el repositorio
            Optional<Booking> bookingOptional = bookingRepository.findById(id);

            if (bookingOptional.isPresent()) {
                // Si la reserva se encuentra, conviértela en un DTO y devuélvela
                Booking booking = bookingOptional.get();
                BookingDTO bookingDTO = mapper.convertValue(booking, BookingDTO.class);
                return bookingDTO;
            } else {
                // Si la reserva no se encuentra, lanza una excepción ResourceNotFoundException
                throw new ResourceNotFoundException("Reserva no encontrada con el ID: " + id);
            }
        } catch (Exception e) {
            // Captura cualquier excepción inesperada y lanza una DatabaseException
            throw new DatabaseException("Error al buscar la reserva con ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public List<BookingDTO> listBooking() {
        List<Booking> bookings = bookingRepository.findAll();
        // Convierte las entidades Booking a DTOs (BookingDTO) y devuélvelas en una lista
        return bookings.stream()
                .map(booking -> mapper.convertValue(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> findBookingsByAuthenticatedUser() {
        /*try {
            // Obtener el usuario autenticado de la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Utilizar la consulta personalizada para recuperar las reservas del usuario autenticado
            List<Booking> bookings = bookingRepository.findAllBookingsByUser(authenticatedUser);

            // Convertir las entidades Booking a DTOs (BookingDTO) y devolverlas en una lista
            return bookings.stream()
                    .map(booking -> mapper.convertValue(booking, BookingDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Captura cualquier excepción inesperada y lanza una DatabaseException
            throw new DatabaseException("Error al listar las reservas " + e.getMessage());
        }*/
//        return null;
  //  }

}
/* @Override
    public BookingDTO createBooking(BookingDTO bookingDTO) throws DuplicateException, DatabaseException, BadRequestException, ResourceNotFoundException {
        try {
            // Obtener el usuario autenticado de la sesión. Primero si evalua si esta iniciada la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Cargar una instancia gestionada del usuario desde la base de datos
            authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear una instancia de la entidad Booking
            Booking booking = mapper.convertValue(bookingDTO, Booking.class);

            // Generar un código de reserva único
            String reservationCode;
            do {
                reservationCode = bookingCodeGenerator.generateUniqueReservationCode();
            } while (bookingRepository.existsByBookingCode(reservationCode));

            // Asignar el código de reserva generado
            booking.setBookingCode(reservationCode);

            // Verificar si hay suficientes productos disponibles en el stock
            for (ProductDTO productDTO : bookingDTO.getProducts()) {
                Product product = productRepository.findById(productDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                // Obtener la lista de OccupiedDateDTO
                List<OccupiedDateDTO> occupiedDateDTOs = getOccupiedDatesForProduct(product.getId());

                // Convertir la lista de OccupiedDateDTO a una lista de LocalDate
                List<LocalDate> occupiedDates = occupiedDateDTOs.stream()
                        .flatMap(dto -> Stream.of(dto.getStartDate(), dto.getEndDate()))
                        .collect(Collectors.toList());                boolean isProductAvailable = checkProductAvailability(bookingDTO.getStartDate(), bookingDTO.getEndDate(), occupiedDates);

                if (!isProductAvailable) {
                    throw new BadRequestException("El producto no está disponible en las fechas seleccionadas");
                }

                if (product.getStock() < 1) {
                    throw new NotEnoughStockException("No hay suficientes productos disponibles");
                }

                // Reducir la cantidad en el stock
                product.setStock(product.getStock() - 1);
            }

            // Convertir la lista de ProductDTO a una lista de Product
            List<Product> products = new ArrayList<>();
            for (ProductDTO productDTO : bookingDTO.getProducts()) {
                Product product = productRepository.findById(productDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                products.add(product);
            }

            // Calcular bookingDays y totalCost
            Integer bookingDays = calculateBookingDays(bookingDTO.getStartDate(), bookingDTO.getEndDate());
            Double totalCost = calculateTotalCost(products, bookingDays);

            // Establecer bookingDays y totalCost en la entidad Booking
            booking.setBookingDays(bookingDays);
            booking.setTotalCost(totalCost);


            // Establecer la relación entre la reserva y el usuario autenticado
            booking.setUser(authenticatedUser);

            // Guardar la reserva en la base de datos
            bookingRepository.save(booking);

            // Convertir la entidad Booking de vuelta a DTO para devolverla
            bookingDTO = mapper.convertValue(booking, BookingDTO.class);

            return bookingDTO;
        } catch (BadRequestException e) {
            throw e;
        } catch (NotEnoughStockException e) {
            throw e;
        }

    }
*/