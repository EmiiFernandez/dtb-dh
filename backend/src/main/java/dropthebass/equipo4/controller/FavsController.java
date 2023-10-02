package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.FavsDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IFavsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favs")
public class FavsController {
    @Autowired
    private IFavsService favsService;

    @PostMapping()
    public ResponseEntity<?> createFavs(@RequestBody FavsDTO favsDTO) {
        try {
            FavsDTO newFavs = favsService.createFavs(favsDTO);
            return ResponseEntity.ok(newFavs);
        } catch (DuplicateException e) {
            return ResponseEntity.badRequest().body("No se puede agregar el producto duplicado a la lista de favoritos.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavs(@PathVariable Long id, @RequestBody FavsDTO favsDTO) {
        try {
            FavsDTO updatedFavs = favsService.deleteFavs(id);
            return ResponseEntity.ok(updatedFavs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.badRequest().body("No se pudo eliminar la lista de favoritos debido a un problema en la base de datos.");
        }
    }

    @GetMapping()
    public ResponseEntity<?> listFavs() {
        try {
            List<FavsDTO> favsDTOList = favsService.listFavs();
            return ResponseEntity.ok(favsDTOList);
        } catch (DatabaseException e) {
            String errorMessage = "Error al listar los productos debido a un problema en la base de datos.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
