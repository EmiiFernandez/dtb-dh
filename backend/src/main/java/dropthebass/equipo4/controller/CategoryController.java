package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.CategoryDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    // Crear una nueva categoría
    @PostMapping()
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            // Intentar crear la categoría y manejar excepciones
            CategoryDTO newCategory = categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok(newCategory);
        } catch (DuplicateException e) {
            return ResponseEntity.badRequest().body("La categoría ya existe.");
        }
    }

    // Buscar una categoría por su ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findCategoryById(@PathVariable Long id) {
        try {
            // Intentar encontrar la categoría y manejar excepciones
            CategoryDTO categoryDTO = categoryService.findCategoryId(id);
            return ResponseEntity.ok(categoryDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Modificar una categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) throws ResourceNotFoundException {
        try {
            categoryService.modifyCategory(id, categoryDTO);
            return ResponseEntity.ok("Categoría modificada con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar la categoría: " + e.getMessage());
        }
    }

    // Eliminar una categoría por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Categoría eliminada con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.badRequest().body("No se pudo eliminar la categoría debido a que tiene productos asociados: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la categoría: " + e.getMessage());
        }
    }

    // Listar todas las categorías
    @GetMapping()
    public ResponseEntity<?> listCategories() {
        try {
            List<CategoryDTO> categoryDTOS = categoryService.listCategories();
            return ResponseEntity.ok(categoryDTOS);
        } catch (DatabaseException e) {
            String errorMessage = "Error al listar las categorias debido a un problema en la base de datos: ";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
        }
    }
}
