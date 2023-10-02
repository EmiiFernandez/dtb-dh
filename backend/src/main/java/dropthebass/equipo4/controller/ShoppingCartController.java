package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.ShoppingCartDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IShoppingCartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    private IShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(IShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping()
    public ResponseEntity<List<ShoppingCartDTO>>getListByUser() throws ResourceNotFoundException {
        return new ResponseEntity<>(shoppingCartService.getListByUser(), HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<?> addProduct(@Valid @RequestBody ShoppingCartDTO shoppingCartDTO,
                                              BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            // Obtener una lista de errores de validación
            List<String> validationErrors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
        }

        shoppingCartService.addProduct(shoppingCartDTO);
        return new ResponseEntity<>("Producto agregado", HttpStatus.OK);
    }
    @DeleteMapping("/{item_id}")
    public ResponseEntity<?> removeProduct(@PathVariable("item_id")Long id) throws ResourceNotFoundException {
        try {
            this.shoppingCartService.removeProduct(id);
            return new ResponseEntity<>("Producto eliminado", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countByClient() throws ResourceNotFoundException {
        // Obtiene la información de autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica si la autenticación contiene detalles del usuario y es una instancia de UserDetails
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String userEmail = userDetails.getUsername();
            Long itemCount = shoppingCartService.getCountByUser(userEmail);
            return new ResponseEntity<>(itemCount, HttpStatus.OK);
        }
        return null;
    }
    @DeleteMapping("/clean")
    public ResponseEntity<?> cleanShoppingCart() throws ResourceNotFoundException {
        // Obtiene la información de autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica si la autenticación contiene detalles del usuario y es una instancia de UserDetails
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String userEmail = userDetails.getUsername();

            // Llama al servicio para limpiar el carrito de compras del usuario
            shoppingCartService.cleanShoppingCart(userEmail);

            return new ResponseEntity<>("Carrito de compras limpiado", HttpStatus.OK);
        } else {
            // Manejar el caso en el que no se pudo obtener el correo electrónico del usuario autenticado
            return new ResponseEntity<>("No se pudo obtener el correo electrónico del usuario autenticado", HttpStatus.BAD_REQUEST);
        }
    }
}
