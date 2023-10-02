package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.BrandDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
        @Autowired
        private IBrandService brandService;

    // Listar todas las marcas
    @GetMapping()
    public ResponseEntity<List<BrandDTO>> listBrands() {
        try {
            // Intentar listar las marcas y manejar excepciones
            List<BrandDTO> brandDTOS = brandService.listBrands();
            return ResponseEntity.ok(brandDTOS);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Buscar una marca por su ID
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable Long id) {
        try {
            // Intentar encontrar la marca y manejar excepciones
            BrandDTO brandDTO = brandService.findBrandId(id);
            return ResponseEntity.ok(brandDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Crear una nueva marca
    @PostMapping()
    public ResponseEntity<?> createBrand(@RequestBody BrandDTO brandDTO) {
        try {
            // Intentar crear la marca y manejar excepciones
            BrandDTO newBrand = brandService.createBrand(brandDTO);
            return ResponseEntity.ok(newBrand);
        } catch (DuplicateException e) {
            return ResponseEntity.badRequest().body("La marca ya existe.");
        }
    }


    // Modificar una marca existente
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        try {
            brandService.modifyBrand(id, brandDTO);
            return ResponseEntity.ok("Marca modificada con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar la marca: " + e.getMessage());
        }
    }

    // Eliminar una marca por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok("Marca eliminada con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.badRequest().body("No se pudo eliminar la marca debido a que tiene productos asociados: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la marca: " + e.getMessage());
        }
    }


    /*
        @PostMapping(
                value = "{brandId}/brand-image",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
        public void uploadBrandImage(
                @PathVariable("brandId") Long brandId,
                @RequestParam("file")MultipartFile file
        ){
            brandService.uploadBrandImage(brandId,file);
        }

        @GetMapping("{brandId}/brand-image")
        public ResponseEntity<InputStreamResource> getBrandImage(@PathVariable("brandId") Long brandId) throws ResourceNotFoundException {

            byte[] imageBytes = brandService.getBrandImage(brandId);
            MediaType mediaType = MediaType.IMAGE_JPEG;
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        }
*/
    }
