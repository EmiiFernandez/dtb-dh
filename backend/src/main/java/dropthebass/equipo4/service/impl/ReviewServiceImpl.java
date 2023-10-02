package dropthebass.equipo4.service.impl;

import dropthebass.equipo4.dto.ReviewDTO;
import dropthebass.equipo4.entity.*;
import dropthebass.equipo4.exeptions.BadRequestException;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.*;
import dropthebass.equipo4.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements IReviewService {

    private IReviewRepository reviewRepository;

    private IUserRepository userRepository;

    private IDetailBookingRepository detailBookingRepository;

    private IProductRepository productRepository;


    @Autowired
    public ReviewServiceImpl(IReviewRepository reviewRepository, IUserRepository userRepository, IDetailBookingRepository detailBookingRepository, IProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.detailBookingRepository = detailBookingRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ReviewDTO createReview(String username, Long productId, ReviewDTO reviewDTO) throws ResourceNotFoundException, BadRequestException, DatabaseException {
        try {

            // Buscar el producto por su ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));


            // Obtener el usuario autenticado de la sesión. Primero, evalúa si está iniciada la sesión
            // Buscar el usuario por su nombre de usuario
            User authenticatedUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Verificar si el usuario ya ha reservado el producto
            boolean hasReservedProduct = detailBookingRepository.existsByUserIdAndProductId(authenticatedUser.getId(), productId);
            if (!hasReservedProduct) {
                throw new ResourceNotFoundException("El usuario no ha reservado este producto");
            }

            // Verificar si el usuario ya ha revisado el producto
            boolean hasReviewedProduct = reviewRepository.existsByUserIdAndProductId(authenticatedUser.getId(), product.getId());
            if (hasReviewedProduct) {
                throw new BadRequestException("El usuario ya ha dejado una reseña para este producto");
            }


            // Crear una instancia de la entidad Review y configurar los atributos
            Review newReview = new Review();
            newReview.setProductScoring(reviewDTO.getProductScoring());
            newReview.setComments(reviewDTO.getComments());

            // Obtener la fecha actual
            ZoneId timeZone = ZoneId.of("America/Argentina/Buenos_Aires");
            Instant currentInstant = Instant.now();
            Date currentDate = Date.from(currentInstant.atZone(timeZone).toInstant());

            newReview.setReviewDate(currentDate);


            // Asignar el producto
            newReview.setProduct(product);

            // Asignar el usuario autenticado como autor de la reseña
            newReview.setUser(authenticatedUser);

            // Guardar la reseña en la base de datos
            Review savedReview = reviewRepository.save(newReview);

            // Crear un nuevo DTO a partir de la reseña guardada para retornar
            ReviewDTO newReviewDTO = new ReviewDTO();
            newReviewDTO.setProductScoring(savedReview.getProductScoring());
            newReviewDTO.setReviewDate(savedReview.getReviewDate());
            newReviewDTO.setComments(savedReview.getComments());
            newReviewDTO.setUserFullName(authenticatedUser.getName() + " " + authenticatedUser.getLastname());
            newReviewDTO.setProductId(savedReview.getProduct().getId());

            return newReviewDTO;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error al crear la reseña: " + e.getMessage());
        }
    }

    @Override
    public List<ReviewDTO> listReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream()
                .map(review -> {
                    // El código en este bloque se ejecuta para cada objeto "review" en la lista "reviews"
                    // Se crea un nuevo objeto "ReviewDTO" para almacenar los datos de la reseña actual
                    ReviewDTO reviewDTO = new ReviewDTO();

                    // Se obtiene el objeto "User" relacionado con la revisión actual
                    User user = review.getUser();

                    // Se verifica si el objeto "user" no es nulo
                    if (user != null) {
                        // Si "user" no es nulo, se obtiene el nombre completo del usuario
                        // concatenando su nombre y apellido, y se establece en "reviewDTO"
                        reviewDTO.setUserFullName(user.getName() + " " + user.getLastname());
                    }

                    // Se establecen otros atributos de "reviewDTO" utilizando métodos getter de "review"
                    reviewDTO.setProductScoring(review.getProductScoring());
                    reviewDTO.setReviewDate(review.getReviewDate());
                    reviewDTO.setComments(review.getComments());
                    reviewDTO.setProductId(review.getProduct().getId());

                    // El objeto "reviewDTO" creado se devuelve como resultado de la función "map"
                    return reviewDTO;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Double calculateAverageProductScoring(Long productId) throws ResourceNotFoundException {
        // Buscar el producto por su ID
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Producto no encontrado con el código: " + productId);
        }

        return reviewRepository.calculateAverageProductScoring(productId);
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId) throws ResourceNotFoundException {
        // Buscar el producto por su ID
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Producto no encontrado con el código: " + productId);
        }

        List<Review> reviews = reviewRepository.findReviewsByProductId(productId);

        return reviews.stream()
                .map(review -> {
                    ReviewDTO reviewDTO = new ReviewDTO();
                    // Mapea los campos de Review a ReviewDTO aquí
                    reviewDTO.setUserFullName(review.getUser().getName() + " " + review.getUser().getLastname());
                    reviewDTO.setProductScoring(review.getProductScoring());
                    reviewDTO.setReviewDate(review.getReviewDate());
                    reviewDTO.setComments(review.getComments());
                    reviewDTO.setProductId(review.getProduct().getId());
                    return reviewDTO;
                })
                .collect(Collectors.toList());
    }
}

    /*private String getAuthenticatedUser(HttpSession session) throws ResourceNotFoundException {
        // Obtener el correo electrónico del usuario autenticado desde la sesión
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }

        return userEmail;
    }
}

  /*  @Override
    public ReviewDTO createReviewForProductInBooking(Long bookingId, Long productId, ReviewDTO inputReviewDTO)
            throws ResourceNotFoundException, BadRequestException, DatabaseException {
        try {
            // Obtener el usuario autenticado de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Verificar si el usuario está autenticado
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ResourceNotFoundException("El usuario no está autenticado.");
            }

            // Obtener el correo electrónico del usuario autenticado
            String userEmail = authentication.getName();

            // Buscar la reserva por su ID
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

            if (!optionalBooking.isPresent()) {
                throw new ResourceNotFoundException("Reserva no encontrada con el código: " + bookingId);
            }

            Booking booking = optionalBooking.get();

            // Verificar si el usuario autenticado coincide con el usuario que realizó la reserva
            if (!userEmail.equals(booking.getUser().getEmail())) {
                throw new BadRequestException("El usuario autenticado no es el propietario de la reserva.");
            }

            // Buscar el producto por su ID
            Optional<Product> optionalProduct = productRepository.findById(productId);

            if (!optionalProduct.isPresent()) {
                throw new ResourceNotFoundException("Producto no encontrado con el código: " + productId);
            }

            Product product = optionalProduct.get();

            // Verificar si el producto está asociado a la reserva
            if (!booking.getProducts().contains(product)) {
                throw new BadRequestException("El producto no está asociado a la reserva.");
            }*//*

            // Verificar si el usuario ya ha realizado una reseña para este producto
            List<Review> existingReviews = reviewRepository.findByUserEmailAndProductId(userEmail, productId);
            if (!existingReviews.isEmpty()) {
                throw new BadRequestException("El usuario ya ha realizado una reseña para este producto.");
            }

            // Ahora que hemos verificado la reserva y el producto, podemos proceder a crear la reseña.
            ReviewDTO createdReviewDTO = createReviewForProduct(booking, userEmail, product, inputReviewDTO);
            return createdReviewDTO;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error al generar reseña: " + e.getMessage());
        }
    }

    private ReviewDTO createReviewForProduct(Booking booking, String userEmail, Product product, ReviewDTO reviewDTO)
            throws BadRequestException, ResourceNotFoundException {
        // Obtener el usuario autenticado de Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }

        // Obtener el correo electrónico del usuario autenticado
        userEmail = authentication.getName();

        // Crear una instancia de la entidad Review y configurar los atributos
        Review newReview = new Review();
        newReview.setProductScoring(reviewDTO.getProductScoring());
        newReview.setComments(reviewDTO.getComments());
        newReview.setReviewDate(LocalDate.now());
        newReview.setProduct(product); // Asignar el producto
        newReview.setUser(booking.getUser());

        if (newReview.getProductScoring() < 1 || newReview.getProductScoring() > 5) {
            throw new BadRequestException("La valoración del producto debe ser entre 1 y 5");
        }

        // Guardar la reseña en la base de datos
        Review savedReview = reviewRepository.save(newReview);

        // Crear un objeto ReviewDTO a partir de la reseña y devolverlo
        ReviewDTO createdReviewDTO = new ReviewDTO();
        createdReviewDTO.setUserFullName(booking.getUser().getName() + " " + booking.getUser().getLastname());
        createdReviewDTO.setProductScoring(savedReview.getProductScoring());
        createdReviewDTO.setReviewDate(savedReview.getReviewDate());
        createdReviewDTO.setComments(savedReview.getComments());
        createdReviewDTO.setProductId(product.getId());

        return createdReviewDTO;
    }




   */