package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.ProductDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IProductService {
    public void createProduct(ProductDTO productDTO) throws DuplicateException, DatabaseException, ResourceNotFoundException;

    public ProductDTO findProductId(Long id) throws ResourceNotFoundException;

    public ProductDTO modifyProduct(Long id, ProductDTO productDTO) throws ResourceNotFoundException;

    public void deleteProduct(Long id) throws ResourceNotFoundException;

    public List<ProductDTO> listProduct();

    public List<ProductDTO> searchProductsByName(String keyword);

    /*
    void uploadProductImage(Long productId, MultipartFile file);

    public byte[] getProductImage(Long productId, int number) throws ResourceNotFoundException ;

    public List<byte[]> getProductImages(Long productId) throws ResourceNotFoundException;
    */

    }

