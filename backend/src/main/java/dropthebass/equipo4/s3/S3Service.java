package dropthebass.equipo4.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3;

    @Autowired
    private AmazonS3 amazonS3;


    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    @Value("${aws.s3.buckets.customer}")
    private String awsBucketName;


    public void putObject( String key, byte[] file){
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(awsBucketName)
                .key(key)
                .build();

        s3.putObject(objectRequest, RequestBody.fromBytes(file));
    }

    public byte[] getObject( String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(awsBucketName).key(key).build();

        ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);

        try {
            byte[] bytes = res.readAllBytes();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getObjectsFromS3( String entityString, String idEntity ) {
        System.out.println("Voy a buscar: "+awsBucketName+entityString +"/"+idEntity);
        ListObjectsV2Result result = amazonS3.listObjectsV2(awsBucketName,entityString +"/"+idEntity );
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        List<String> list = objects.stream().map(item -> {
            return item.getKey();
        }).collect(Collectors.toList());
        return list;
    }
    public void deleteObject( String objectKey) {
        try {
            amazonS3.deleteObject(awsBucketName, objectKey);
            System.out.println("Objeto " + objectKey + " eliminado correctamente del bucket " + awsBucketName);
        } catch (AmazonServiceException e) {
            // Maneja las excepciones de Amazon S3, si es necesario
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el objeto: " + e.getMessage());
        }
    }

    public boolean verifyEntityString(String entity){
        String[] opciones = {"brand-images", "product-images", "category-images", "user-images"};
        return Arrays.asList(opciones).contains(entity);
    }



}
