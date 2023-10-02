package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.BrandDTO;
import dropthebass.equipo4.entity.Brand;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBrandService {
    public BrandDTO createBrand(BrandDTO brandDTO) throws DuplicateException, DatabaseException;
    public BrandDTO findBrandId (Long id) throws ResourceNotFoundException;
    public Brand modifyBrand (Long id, BrandDTO brandDTO) throws ResourceNotFoundException, DatabaseException ;
    public void deleteBrand (Long id) throws ResourceNotFoundException;
    public List<BrandDTO> listBrands();
}
