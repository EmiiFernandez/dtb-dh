package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.BrandDTO;
import dropthebass.equipo4.dto.ShoppingCartDTO;
import dropthebass.equipo4.entity.Product;
import dropthebass.equipo4.entity.ShoppingCart;
import dropthebass.equipo4.entity.User;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IProductRepository;
import dropthebass.equipo4.repository.IShoppingCartRepository;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.IShoppingCartService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShoppingCartServiceImpl implements IShoppingCartService {

    // Repositorio de ShoppingCart para acceder a los datos de carrito de compras
    private IShoppingCartRepository shoppingCartRepository;

    // ObjectMapper para convertir entre DTO y entidad
    private ObjectMapper mapper;

    // Repositorio de usuarios para acceder a los datos del usuario
    private IUserRepository userRepository;

    // Repositorio de productos para acceder a los datos del producto
    private IProductRepository productRepository;

    // HttpSession para obtener la sesión del usuario autenticado
    private HttpSession session;

    @Autowired
    public ShoppingCartServiceImpl(IShoppingCartRepository shoppingCartRepository,
                                   ObjectMapper mapper, IUserRepository userRepository,
                                   IProductRepository productRepository, HttpSession session) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.session = session;
    }


    @Override
    public void addProduct(ShoppingCartDTO shoppingCartDTO) throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión. Primero si evalua si esta iniciada la sesión
        User authenticatedUser = getAuthenticatedUser(session);

        // Cargar una instancia gestionada del usuario desde la base de datos
        authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Obtén el id del producto desde el ShoppingCartDTO
        Long productId = shoppingCartDTO.getProduct().getId();

        // Busca el Product correspondiente en la base de datos por su id
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));

        // fechas de inicio y fin de la reserva del DTO
        Date startBooking = shoppingCartDTO.getStartBooking();
        Date endBooking = shoppingCartDTO.getEndBooking();

        // Crea un nuevo ShoppingCart y asigna el Product y las fechas de reserva
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAmount(shoppingCartDTO.getAmount());
        shoppingCart.setProduct(product);
        shoppingCart.setUser(authenticatedUser);
        shoppingCart.setStartBooking(startBooking);
        shoppingCart.setEndBooking(endBooking);

        // Guarda el carrito en la base de datos
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public List<ShoppingCartDTO> getListByUser() throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión
        User authenticatedUser = getAuthenticatedUser(session);

        // Obtiene una lista de carritos de compras del usuario por su correo electrónico
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findByUserEmail(authenticatedUser.getEmail());

        // Convierte la lista de entidades ShoppingCart en una lista de DTOs ShoppingCartDTO
        List<ShoppingCartDTO> shoppingCartDTOList = shoppingCarts.stream()
                .map(entity -> mapper.convertValue(entity, ShoppingCartDTO.class))
                .collect(Collectors.toList());

        return shoppingCartDTOList;
    }

    @Override
    public void removeProduct(Long id) throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión
        User authenticatedUser = getAuthenticatedUser(session);

        // Obtener la lista de carritos de compras del usuario
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findByUserEmail(authenticatedUser.getEmail());

        // Buscar el producto en la lista de carritos de compras del usuario por su ID
        Optional<ShoppingCart> optionalShoppingCart = shoppingCarts.stream()
                .filter(cart -> cart.getProduct().getId().equals(id))
                .findFirst();

        // Si se encuentra el carrito de compras con el producto, eliminarlo
        optionalShoppingCart.ifPresent(shoppingCartRepository::delete);
    }

    @Override
    public Long getCountByUser(String userEmail) throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión
        User authenticatedUser = getAuthenticatedUser(session);
        // Obtiene la cantidad de elementos en el carrito de compras del usuario por su correo electrónico
        return shoppingCartRepository.countByUserEmail(authenticatedUser.getEmail());
    }

    @Override
    public void cleanShoppingCart(String userEmail) throws ResourceNotFoundException {
        // Obtener el usuario autenticado de la sesión
        User authenticatedUser = getAuthenticatedUser(session);

        // Elimina todos los elementos del carrito de compras del usuario por su correo electrónico
        shoppingCartRepository.deleteByUserEmail(authenticatedUser.getEmail());
    }



    // Método privado para obtener el usuario autenticado desde la sesión
    private User getAuthenticatedUser(HttpSession session) throws ResourceNotFoundException {
        User userLogged = (User) session.getAttribute("userSession");
        if (userLogged == null) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }
        return userLogged;
    }
}
