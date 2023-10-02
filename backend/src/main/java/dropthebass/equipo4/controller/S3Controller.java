package dropthebass.equipo4.controller;

import dropthebass.equipo4.dto.ProductDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.s3.S3Service;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    S3Service s3Service;

    @Value("${aws.s3.buckets.customer}")
    private String awsBucketName;



    @GetMapping("/{entity}/{entityId}")
    public ResponseEntity<?> listarProductos(@PathVariable String entity, @PathVariable String entityId) {

        if (!s3Service.verifyEntityString(entity)){
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("La entidad "+entity+" no existe, usar: brand-images product-images category-images user-images");
        }

        return new ResponseEntity<List<String>>(s3Service.getObjectsFromS3(entity,entityId),HttpStatus.OK);
    }

    @GetMapping("/{entity}/{entityId}/{imageNumber}")
    public ResponseEntity<?> listarProductos(@PathVariable String entity, @PathVariable String entityId, @PathVariable int imageNumber) {

        if (!s3Service.verifyEntityString(entity)){
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("La entidad "+entity+" no existe, usar: brand-images product-images category-images user-images");
        }

        byte[] imageBytes =  s3Service.getObject( s3Service.getObjectsFromS3(entity,entityId).get(imageNumber));
        MediaType mediaType = MediaType.IMAGE_JPEG;
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @DeleteMapping("/{entity}/{entityId}/{imageNumber}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String entity, @PathVariable String entityId, @PathVariable int imageNumber) {

        if (!s3Service.verifyEntityString(entity)){
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("La entidad "+entity+" no existe, usar: brand-images product-images category-images user-images");
        }

        List<String> list = s3Service.getObjectsFromS3( entity, entityId );
        System.out.println("list is "+ list);
        String key = list.get(imageNumber);
        s3Service.deleteObject(key);
        List<String> list2 = s3Service.getObjectsFromS3( entity, entityId );
        System.out.println("list2 is "+ list2);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Imagen eliminada con exito ");

    }

    @PostMapping(
            value = "/{entity}/{entityId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadImage(
            @PathVariable("entity") String entity,
            @PathVariable("entityId") String entityId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String BrandImageId = UUID.randomUUID().toString();
        s3Service.putObject(entity+"/"+entityId+"/"+BrandImageId,file.getBytes());
    }

    @PutMapping(
            value = "/{entity}/{entityId}/{imageId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void editImage(
            @NotNull @PathVariable("entity") String entity,
            @NotNull @PathVariable("entityId") String entityId,
           @NotNull @PathVariable("imageId") String imageId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        s3Service.putObject(entity+"/"+entityId+"/"+imageId,file.getBytes());
    }









}
