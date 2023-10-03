package dropthebass.equipo4.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images/product")
public class ProductImageController {

    @Value("${product.images.directory}")
    private String imagesDirectory;

 /*   @GetMapping("/{productId}/{imageName:.+}")
    public ResponseEntity<Resource> getImageByName(@PathVariable Long productId, @PathVariable String imageName) {
        try {
            String productDirectory = imagesDirectory + productId + "/";
            Path imagePath = Paths.get(productDirectory, imageName);
            Resource imageResource = new FileSystemResource(imagePath.toFile());

            if (imageResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Cambiar según el tipo de imagen
                        .body(imageResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }*/

    @PostMapping("/{productId}")
    public ResponseEntity<String> uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        try {
            String productDirectory = imagesDirectory + productId + "/";
            String randomFileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(productDirectory, randomFileName);

            // Verifica si la carpeta del producto existe, y si no, créala
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            // Guarda la imagen en el sistema de archivos
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Imagen cargada con éxito. Nombre del archivo: " + randomFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al cargar la imagen.");
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<String>> getAllImagesForProduct(@PathVariable Long productId) {
        try {
            String productDirectory = imagesDirectory + productId + "/";
            File productFolder = new File(productDirectory);

            if (!productFolder.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Obtén la lista de nombres de archivo en la carpeta del producto
            String[] fileNames = productFolder.list();

            if (fileNames != null && fileNames.length > 0) {
                List<String> imageNames = Arrays.asList(fileNames);
                return ResponseEntity.ok(imageNames);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{productId}/{imageLocation}")
    public ResponseEntity<Resource> getImageByLocation(@PathVariable Long productId, @PathVariable int imageLocation) {
        try {
            String productDirectory = imagesDirectory + productId + "/";
            File productFolder = new File(productDirectory);

            if (!productFolder.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Obtén la lista de nombres de archivo en la carpeta del producto
            String[] fileNames = productFolder.list();

            if (fileNames != null && fileNames.length > imageLocation) {
                String imageName = fileNames[imageLocation];
                Path imagePath = Paths.get(productDirectory, imageName);
                Resource imageResource = new FileSystemResource(imagePath.toFile());

                if (imageResource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG) // Cambiar según el tipo de imagen
                            .body(imageResource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
