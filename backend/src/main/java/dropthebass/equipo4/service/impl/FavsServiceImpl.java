package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import dropthebass.equipo4.dto.FavsDTO;
import dropthebass.equipo4.dto.ProductDTO;
import dropthebass.equipo4.entity.*;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IFavsRepository;
import dropthebass.equipo4.repository.IProductRepository;
import dropthebass.equipo4.repository.IUserRepository;
import dropthebass.equipo4.service.IFavsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavsServiceImpl implements IFavsService {

    private IFavsRepository favsRepository;

    private IProductRepository productRepository;

    private IUserRepository userRepository;

    private HttpSession session;

    ObjectMapper mapper;

    @Autowired
    public FavsServiceImpl(IFavsRepository favsRepository, IProductRepository productRepository, IUserRepository userRepository, HttpSession session, ObjectMapper mapper) {
        this.favsRepository = favsRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.session = session;
        this.mapper = mapper;
    }

    @Override
    public FavsDTO createFavs(FavsDTO favsDTO) throws DuplicateException, DatabaseException, ResourceNotFoundException {
        try {
            // Obtener el usuario autenticado de la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Cargar una instancia gestionada del usuario desde la base de datos
            authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Buscar si ya existe una lista de favoritos para este usuario
            Favs listFavs = favsRepository.findFavsByUser(authenticatedUser);

            // Si no existe la lista, crear una nueva
            if (listFavs == null) {
                listFavs = new Favs();
                listFavs.setUser(authenticatedUser);
                listFavs.setProducts(new HashSet<>());
            }

            // Listado de productos
            Set<ProductDTO> productDTOS = favsDTO.getProducts();

            for (ProductDTO productDTO : productDTOS) {
                // Obtener la entidad Product existente por su ID con todos los detalles
                Optional<Product> existingProductOptional = productRepository.findByIdWithDetails(productDTO.getId());

                if (existingProductOptional.isPresent()) {
                    Product existingProduct = existingProductOptional.get();

                    // Verificar si el producto ya está en la lista de favoritos
                    boolean existsProduct = listFavs.getProducts().contains(existingProduct);

                    if (!existsProduct) {
                        listFavs.getProducts().add(existingProduct);
                    }
                } else {
                    throw new ResourceNotFoundException("Producto no encontrado");
                }
            }

            // Guardar la lista de favoritos actualizada en la base de datos
            favsRepository.save(listFavs);

            // Convertir la lista de favoritos actualizada a un DTO y devolverlo
            FavsDTO favsDTOS = mapper.convertValue(listFavs, FavsDTO.class);

            return favsDTOS;
        } catch (Exception e) {
            throw new DatabaseException("Error al crear la lista de favoritos: " + e.getMessage());
        }

    }

    @Override
    public FavsDTO deleteFavs(Long productId) throws ResourceNotFoundException {
        try {
            // Obtener el usuario autenticado de la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Cargar una instancia gestionada del usuario desde la base de datos
            authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Buscar si ya existe una lista de favoritos para este usuario
            Favs listFavs = favsRepository.findFavsByUser(authenticatedUser);

            // Si no existe la lista, lanzar una excepción
            if (listFavs == null) {
                throw new ResourceNotFoundException("No se encontraron favoritos para el usuario.");
            }

            // Obtener el producto por su ID con todos los detalles
            Optional<Product> productToRemoveOptional = productRepository.findByIdWithDetails(productId);

            if (productToRemoveOptional.isPresent()) {
                Product productToRemove = productToRemoveOptional.get();
                listFavs.getProducts().remove(productToRemove);

                // Guardar la lista de favoritos actualizada en la base de datos
                favsRepository.save(listFavs);

                // Convertir la lista de favoritos actualizada a un DTO y devolverlo
                FavsDTO updatedFavsDTO = mapper.convertValue(listFavs, FavsDTO.class);

                return updatedFavsDTO;
            } else {
                throw new ResourceNotFoundException("Producto no encontrado en la lista de favoritos.");
            }
        } catch (Exception e) {
            throw new DatabaseException("Error al modificar la lista de favoritos: " + e.getMessage());
        }
    }

    @Override
    public List<FavsDTO> listFavs() {
        try {
            // Obtener el usuario autenticado de la sesión. Primero si evalúa si está iniciada la sesión
            User authenticatedUser = getAuthenticatedUser(session);

            // Cargar una instancia gestionada del usuario desde la base de datos
            authenticatedUser = userRepository.findByEmail(authenticatedUser.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
/// Utilizar findFavsByUser para obtener la lista de favoritos del usuario
            Favs listFavs = favsRepository.findFavsByUser(authenticatedUser);

// Verificar si la lista de favoritos contiene productos
            if (listFavs == null || listFavs.getProducts().isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron favoritos para el usuario.");
            }

// Convertir la lista de favoritos a DTO
            List<FavsDTO> favsDTOList = new ArrayList<>();
            FavsDTO favsDTO = mapper.convertValue(listFavs, FavsDTO.class);
            favsDTOList.add(favsDTO);

            return favsDTOList;

        } catch (DataAccessException | ResourceNotFoundException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new RuntimeException("Error al intentar listar los productos favoritos debido a un problema en la base de datos." + e.getMessage());
        }
    }

    private User getAuthenticatedUser(HttpSession session) throws ResourceNotFoundException {
        User userLogged = (User) session.getAttribute("userSession");
        if (userLogged == null) {
            throw new ResourceNotFoundException("El usuario no está autenticado.");
        }
        return userLogged;
    }

}
