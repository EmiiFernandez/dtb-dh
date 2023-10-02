package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.BrandDTO;
import dropthebass.equipo4.entity.Brand;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IBrandRepository;
import dropthebass.equipo4.s3.S3Buckets;
import dropthebass.equipo4.s3.S3Service;
import dropthebass.equipo4.service.IBrandService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
@Service
public class BrandServiceImpl implements IBrandService {

    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    @Autowired
    private IBrandRepository brandRepository;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    public BrandServiceImpl(S3Service s3Service, S3Buckets s3Buckets, IBrandRepository brandRepository) {
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
        this.brandRepository = brandRepository;
    }

    // Guarda una marca en la base de datos a partir de un objeto BrandDTO
    private void saveBrand(BrandDTO brandDTO) {
        Brand brand = mapper.convertValue(brandDTO, Brand.class);
        brandRepository.save(brand);
    }

    /**
     * Crea una nueva marca en la base de datos.
     *
     * @param brandDTO Objeto BrandDTO que contiene los datos de la nueva marca.
     * @return La marca recién creada.
     * @throws DuplicateException Si la marca ya existe en la base de datos.
     * @throws DatabaseException Si ocurre un error al intentar guardar la marca en la base de datos.
     */
    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) throws DuplicateException, DatabaseException {
        // Verificar si la marca ya existe por nombre
        Brand existingBrand = brandRepository.findByName(brandDTO.getName());
        if (existingBrand != null) {
            throw new DuplicateException("La marca ya existe.");
        }

        try {
            // Crear una nueva marca y asignar los datos
            Brand newBrand = new Brand();
            newBrand.setName(brandDTO.getName());
            newBrand.setCountry(brandDTO.getCountry());
            newBrand.setWebSite(brandDTO.getWebSite());
            newBrand.setBrandImageId(brandDTO.getBrandImageId());

            // Guardar la nueva marca en la base de datos
            brandRepository.save(newBrand);

            // Devolver el DTO de la marca creada
            return mapper.convertValue(newBrand, BrandDTO.class);
        } catch (Exception e) {
            throw new DatabaseException("No se pudo crear la marca debido a un error en la base de datos.", e);
        }
    }

    /**
     * Encuentra una marca por su ID.
     *
     * @param id El ID de la marca que se desea encontrar.
     * @return El objeto BrandDTO que representa la marca encontrada.
     * @throws ResourceNotFoundException Si no se encontró ninguna marca con el ID proporcionado.
     */
    @Override
    public BrandDTO findBrandId(Long id) throws ResourceNotFoundException {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            return mapper.convertValue(brand.get(), BrandDTO.class);
        } else {
            throw new ResourceNotFoundException("No se encontró la marca con el ID: " + id);
        }
    }

    /**
     * Modifica una marca existente en la base de datos con los datos proporcionados en el objeto BrandDTO.
     *
     * @param brandDTO Objeto BrandDTO que contiene los datos actualizados de la marca.
     * @return
     * @throws RuntimeException Si no se pudo modificar la marca debido a un error en la base de datos.
     */
    @Override
    public Brand modifyBrand(Long id, BrandDTO brandDTO) throws ResourceNotFoundException, DatabaseException {
        // Obtener la marca existente por su ID
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la marca con el ID: " + id));

        // Actualizar los atributos si están presentes en el BrandDTO
        if (brandDTO.getName() != null) {
            brand.setName(brandDTO.getName());
        }
        if (brandDTO.getCountry() != null) {
            brand.setCountry(brandDTO.getCountry());
        }
        if (brandDTO.getWebSite() != null) {
            brand.setWebSite(brandDTO.getWebSite());
        }
        if (brandDTO.getBrandImageId() != null) {
            brand.setBrandImageId(brandDTO.getBrandImageId());
        }

        try {
            // Guardar los cambios en la base de datos
            brandRepository.save(brand);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al modificar la marca en la base de datos", e);
        }
        return brand;
    }

    /**
     * Elimina una marca de la base de datos por su ID.
     *
     * @param id El ID de la marca que se desea eliminar.
     * @throws ResourceNotFoundException Si no se encontró la marca con el ID proporcionado.
     * @throws DatabaseException Si no se pudo eliminar la marca debido a que tiene productos asociados o a un error en la base de datos.
     */
    @Override
    public void deleteBrand(Long id) throws ResourceNotFoundException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la marca con el ID: " + id));

        if (!brand.getProducts().isEmpty()) {
            throw new DatabaseException("No se puede eliminar la marca debido a que tiene productos asociados.");
        }

        try {
            brandRepository.delete(brand);
        } catch (DataAccessException e) {
            throw new DatabaseException("No se pudo eliminar la marca", e);
        }
    }

    /**
     * Lista todas las marcas almacenadas en la base de datos.
     *
     * @return Una lista de objetos BrandDTO que representan las marcas.
     * @throws RuntimeException Si no se pudo listar las marcas debido a un error en la base de datos.
     */
    @Override
    public List<BrandDTO> listBrands() {
        try {
            List<Brand> brands = brandRepository.findAll();
            List<BrandDTO> brandDTOS = new ArrayList<>();

            for (Brand brand : brands) {
                BrandDTO brandDTO = mapper.convertValue(brand, BrandDTO.class);
                brandDTOS.add(brandDTO);
            }
            return brandDTOS;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al intentar listar las marcas.", e);
        }
    }

    @Transactional
    public Brand getOrCreateBrand (BrandDTO brandDTO) throws ResourceNotFoundException {
        if (brandDTO.getId() != null) {
            // Se busca por ID
            return brandRepository.findById(brandDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        } else {
            //Si no agrego el ID, se intenta buscar por nombre
            Brand brand = brandRepository.findByName(brandDTO.getName());

            if (brand == null) {
                //Si no existe, se crea nueva marca
                brand = new Brand(brandDTO.getName(), brandDTO.getCountry(), brandDTO.getWebSite(), new HashSet<>(), brandDTO.getBrandImageId());
                brandRepository.save(brand);
            }
            return brand;
        }
    }

/*
    @Override
    public void uploadBrandImage(Long brandId, MultipartFile file) {

        Optional<Brand> brand = brandRepository.findBrandById(brandId);

        if( brand.isPresent()) {
            String BrandImageId = UUID.randomUUID().toString();
            Brand realBrand = brand.get();
            try {
                s3Service.putObject(
                        s3Buckets.getCustomer(),
                        "brand-images/"+brandId+"/"+BrandImageId,
                                file.getBytes()
                        );

                realBrand.setBrandImageId(BrandImageId);
                System.out.println("Real brand is: "+realBrand.toString());
                brandRepository.save(realBrand);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] getBrandImage(Long brandId) throws ResourceNotFoundException {
        BrandDTO brand = findBrandId(brandId);

        String brandImageId = brand.getBrandImageId();


         byte[] brandImage = s3Service.getObject(
                 s3Buckets.getCustomer(),
                 "brand-images/"+brandId +"/"+ brandImageId
         );

         return brandImage;
    }
*/
}
