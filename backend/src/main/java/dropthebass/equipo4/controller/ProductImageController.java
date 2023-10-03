package dropthebass.equipo4.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
@RestController
@RequestMapping("/images/product")
public class ProductImageController {

    @GetMapping("/{id}/{file}")
    public ResponseEntity<Resource> getGitHubProductImage(
            @PathVariable Long id,
            @PathVariable String nombre
    ) {
        try {
            // Construye la URL directa a la imagen en GitHub
            String githubImageUrl = "https://raw.githubusercontent.com/EmiiFernandez/dtb-dh-img/main/img/product/"
                    + id + "/" + nombre + ".jpg";

            // Crea un recurso a partir de la URL
            UrlResource imageResource = new UrlResource(new URI(githubImageUrl));

            if (imageResource.exists()) {
                // Devuelve la imagen con el tipo de contenido adecuado
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Cambia el tipo de contenido según el formato de tus imágenes
                        .body(imageResource);
            } else {
                // Manejo de error si la imagen no existe
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            // Manejo de errores de URL
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            // Manejo de errores de lectura de archivo
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
