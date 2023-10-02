package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.FeatureDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/features")
public class FeatureController {

    @Autowired
    private IFeatureService featureService;

    @PostMapping()
    public ResponseEntity<?> createFeature(@RequestBody FeatureDTO featureDTO) {
        try {
            // Intentar crear la característica y manejar excepciones
            FeatureDTO newFeature = featureService.createFeature(featureDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newFeature);
        } catch (DuplicateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La característica ya existe.");
        }
    }

    // Buscar una característica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<FeatureDTO> findFeatureById(@PathVariable Long id) {
        try {
            // Intentar encontrar la característica y manejar excepciones
            FeatureDTO featureDTO = featureService.findFeatureId(id);
            return ResponseEntity.ok(featureDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Modificar una característica existente
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyFeature(@PathVariable Long id, @RequestBody FeatureDTO featureDTO) throws ResourceNotFoundException {
        try {
            featureService.modifyFeature(id, featureDTO);
            return ResponseEntity.ok("Característica característica con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar la característica: " + e.getMessage());
        }
    }

    // Eliminar una característica por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeature(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            featureService.deleteFeature(id);
            return ResponseEntity.ok("Característica eliminada con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.badRequest().body("No se pudo eliminar la característica debido a que tiene productos asociados: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la característica: " + e.getMessage());
        }
    }

    // Listar todas las características
    @GetMapping()
    public ResponseEntity<?> listFeatures() {
        try {
            List<FeatureDTO> featureDTOS = featureService.listFeatures();
            return ResponseEntity.ok(featureDTOS);
        } catch (DatabaseException e) {
            String errorMessage = "Error al listar las características debido a un problema en la base de datos: ";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
        }
    }
}
