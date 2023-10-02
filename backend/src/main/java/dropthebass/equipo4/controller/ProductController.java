package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.ProductDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) throws ResourceNotFoundException {
        try {
            productService.createProduct(productDTO);
            return ResponseEntity.ok("Producto creado con éxito");
        } catch (DuplicateException e) {
            return ResponseEntity.badRequest().body("El producto ya existe.");
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el producto: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el producto debido a un error en la base de datos: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findProductId(@PathVariable Long id) {
        try {
            ProductDTO productDTO = productService.findProductId(id);
            return ResponseEntity.ok(productDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) throws ResourceNotFoundException {

        try {
            productService.modifyProduct(id, productDTO);
            return ResponseEntity.ok("Producto modificado con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Producto eliminado con éxito");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> listarProductos() {
        try {
            List<ProductDTO> productDTOS = productService.listProduct();
            return ResponseEntity.ok(productDTOS);
        } catch (DatabaseException e) {
            String errorMessage = "Error al listar los productos debido a un problema en la base de datos: ";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<ProductDTO> searchProductsByName(@RequestParam String keyword) {
        List<ProductDTO> productDTOs = productService.searchProductsByName(keyword);
        return productDTOs;
    }

    /*
    @PostMapping(
            value = "{productId}/product-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadBrandImage(
            @PathVariable("productId") Long productId,
            @RequestParam("file") MultipartFile file
    ){
        productService.uploadProductImage(productId,file);
    }

    @GetMapping("{productId}/product-image/{photoNumber}")
    public ResponseEntity<InputStreamResource> getBrandImage(@PathVariable("productId") Long productId,
                                                             @PathVariable("photoNumber") int photoNumber) throws ResourceNotFoundException {

        byte[] imageBytes = productService.getProductImage(productId,photoNumber);
        MediaType mediaType = MediaType.IMAGE_JPEG;
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

*/
}
