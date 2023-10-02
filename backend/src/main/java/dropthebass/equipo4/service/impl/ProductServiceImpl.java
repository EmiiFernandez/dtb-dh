package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.BrandDTO;
import dropthebass.equipo4.dto.CategoryDTO;
import dropthebass.equipo4.dto.FeatureDTO;
import dropthebass.equipo4.dto.ProductDTO;
import dropthebass.equipo4.entity.*;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IBrandRepository;
import dropthebass.equipo4.repository.ICategoryRepository;
import dropthebass.equipo4.repository.IFeatureRepository;
import dropthebass.equipo4.repository.IProductRepository;
import dropthebass.equipo4.s3.S3Buckets;
import dropthebass.equipo4.s3.S3Service;
import dropthebass.equipo4.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {
    private IProductRepository productRepository;

    private IBrandRepository brandRepository;

    private ICategoryRepository categoryRepository;

    private BrandServiceImpl brandService;

    private CategoryServiceImpl categoryService;

    private FeatureServiceImpl featureService;

    private IFeatureRepository featureRepository;
    ObjectMapper mapper;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;


    @Autowired
    public ProductServiceImpl(IProductRepository productRepository, IBrandRepository brandRepository, ICategoryRepository categoryRepository, BrandServiceImpl brandService, CategoryServiceImpl categoryService, FeatureServiceImpl featureService, IFeatureRepository featureRepository, ObjectMapper mapper, S3Service s3Service, S3Buckets s3Buckets) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.featureService = featureService;
        this.featureRepository = featureRepository;
        this.mapper = mapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    /**
     * Crea un nuevo producto en la base de datos.
     *
     * @param productDTO Objeto ProductDTO que contiene los datos del nuevo producto.
     * @throws DuplicateException Si ya existe un producto con el mismo nombre en la misma marca.
     * @throws DatabaseException Si ocurre un error al intentar guardar el producto en la base de datos.
     */
    @Override
    public void createProduct(ProductDTO productDTO) throws DuplicateException, DatabaseException {
        try {
            // Obtener o crear la marca, categoría y características según el DTO
            Brand brand = brandService.getOrCreateBrand(productDTO.getBrand());
            Category category = categoryService.getOrCreateCategory(productDTO.getCategory());

            // Obtener o crear las características según los DTOs
            Set<Feature> features = new HashSet<>();
            for (FeatureDTO featureDTO : productDTO.getFeatures()) {
                Feature feature = featureService.getOrCreateFeature(featureDTO);
                features.add(feature);
            }

            // Verificar si existen productos idénticos en la misma marca y categoría
            List<Product> productList = productRepository.findByNameAndBrandOrNameAndCategory(productDTO.getName(), brand.getId(), category.getId());

            for (Product product : productList) {
                if (product.getBrand().equals(brand)) {
                    throw new DuplicateException("Producto ya existe con el mismo nombre en la misma marca");
                }
            }


            // Crear el producto y asignar la marca, categoría y características
            Product product = new Product(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getStock(), brand, category );
            product.setFeatures(features); // Asignar las características al producto
            productRepository.save(product);

        } catch (ResourceNotFoundException e) {
            // Manejar la excepción de recurso no encontrado
            throw new DatabaseException("No se pudo crear el producto debido a un error en la base de datos." + e.getMessage());
        }
    }
    /**
     * Encuentra un producto por su ID.
     *
     * @param id El ID del producto que se desea encontrar.
     * @return El objeto ProductDTO que representa el producto encontrado.
     * @throws ResourceNotFoundException Si no se encontró ningún producto con el ID proporcionado.
     */
    @Override
    public ProductDTO findProductId(Long id) throws ResourceNotFoundException {
        // Buscar el producto por su ID en la base de datos
        System.out.println("antes de buscar");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        System.out.println("despues de buscar");


        // Convertir las entidades relacionadas (marca y categoría) en DTOs
        BrandDTO brandDTO = mapper.convertValue(product.getBrand(), BrandDTO.class);
        CategoryDTO categoryDTO = mapper.convertValue(product.getCategory(), CategoryDTO.class);

        // Convertir las entidades de características en DTOs
        Set<FeatureDTO> featureDTOS = new HashSet<>();
        for (Feature feature : product.getFeatures()) {
            FeatureDTO featureDTO = mapper.convertValue(feature, FeatureDTO.class);
            featureDTOS.add(featureDTO);
        }

        // Convertir el producto y asignar los DTOs de la marca, categoría y características
        ProductDTO productDTO = mapper.convertValue(product, ProductDTO.class);
        productDTO.setBrand(brandDTO);
        productDTO.setCategory(categoryDTO);
        productDTO.setFeatures(featureDTOS);


        return productDTO; // Retornar el DTO del producto encontrado
    }

    /**
     * Modifica un producto existente en la base de datos con los datos proporcionados en el objeto ProductDTO.
     *
     * @param id El ID del producto que se desea modificar.
     * @param productDTO Objeto ProductDTO que contiene los datos actualizados del producto.
     * @return El producto modificado en forma de ProductDTO.
     * @throws ResourceNotFoundException Si no se pudo encontrar el producto por el ID proporcionado.
     * @throws DatabaseException Si ocurre un error al intentar guardar los cambios en la base de datos.
     */
    @Override
    public ProductDTO modifyProduct(Long id, ProductDTO productDTO) throws ResourceNotFoundException, DatabaseException {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con ID: " + id));

            // Actualizar los atributos del producto con los valores del DTO, si están presentes en el DTO
            if (productDTO.getName() != null) {
                product.setName(productDTO.getName());
            }
            if (productDTO.getDescription() != null) {
                product.setDescription(productDTO.getDescription());
            }
            if (productDTO.getPrice() != null) {
                product.setPrice(productDTO.getPrice());
            }
            if (productDTO.getStock() != null) {
                product.setStock(productDTO.getStock());
            }

            if (productDTO.getBrand() != null) {
                Brand brand = brandService.getOrCreateBrand(productDTO.getBrand());
                product.setBrand(brand);
            }
            if (productDTO.getCategory() != null) {
                Category category = categoryService.getOrCreateCategory(productDTO.getCategory());
                product.setCategory(category);
            }
            // Agregar características al producto si están presentes en el DTO
            if (productDTO.getFeatures() != null && !productDTO.getFeatures().isEmpty()) {
                for (FeatureDTO featureDTO : productDTO.getFeatures()) {
                    Feature feature = featureService.getOrCreateFeature(featureDTO);
                    product.addFeature(feature);
                }
            }

            // Guardar los cambios en el producto
            productRepository.save(product);

            // Convertir el producto modificado a ProductDTO
            return mapper.convertValue(product, ProductDTO.class);
        } catch (ResourceNotFoundException e) {
            // Manejar la excepción de recurso no encontrado
            throw new ResourceNotFoundException("No se pudo modificar el producto debido a un error en la base de datos.");
        }
    }

    /**
     * Elimina un producto de la base de datos por su ID.
     *
     * @param id El ID del producto que se desea eliminar.
     * @throws ResourceNotFoundException Si no se encontró el producto por el ID proporcionado.
     * @throws DatabaseException Si ocurre un error al intentar eliminar el producto de la base de datos.
     */
    @Override
    public void deleteProduct(Long id) throws ResourceNotFoundException, DatabaseException {
        try {
            // Buscar el producto por su ID en la base de datos
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con ID: " + id));

            // Eliminar las entradas relacionadas en la tabla product_feature
            for (Feature feature : product.getFeatures()) {
                feature.getProducts().remove(product);
                featureRepository.save(feature); // Guardar la entidad actualizada
            }
            // Obtener la categoría y la marca asociadas con el producto
            Category category = product.getCategory();
            Brand brand = product.getBrand();

            // Si el producto tiene una categoría, eliminar la relación entre el producto y la categoría
            if (category != null) {
                category.removeProduct(product);
            }

            // Si el producto tiene una marca, eliminar la relación entre el producto y la marca
            if (brand != null) {
                brand.removeProduct(product);
            }

            // Eliminar el producto desde el repositorio
            productRepository.deleteById(id);

        } catch (DataAccessException e) {
            String message = e.getMessage();
            // Manejar la excepción de acceso a la base de datos
            throw new DatabaseException("No se pudo eliminar el producto debido a un error en la base de datos.");
        }
    }

    /**
     * Lista todos los productos almacenados en la base de datos.
     *
     * @return Una lista de objetos ProductDTO que representan los productos.
     * @throws DatabaseException Si ocurre un error al intentar listar los productos en la base de datos.
     */
    @Override
    public List<ProductDTO> listProduct() throws DatabaseException {
        try {
            // Obtener todos los productos de la base de datos
            List<Product> products = productRepository.findAll();

            // Crear una lista para almacenar los objetos ProductDTO resultantes
            List<ProductDTO> productDTOS = new ArrayList<>();

            // Iterar a través de cada producto en la lista de productos
            for (Product product : products) {
                // Convertir el objeto Product en un objeto ProductDTO usando ObjectMapper
                ProductDTO productDTO = mapper.convertValue(product, ProductDTO.class);

                // Obtener la marca asociada con el producto y convertirla en un objeto BrandDTO
                BrandDTO brandDTO = mapper.convertValue(product.getBrand(), BrandDTO.class);

                // Obtener la categoría asociada con el producto y convertirla en un objeto CategoryDTO
                CategoryDTO categoryDTO = mapper.convertValue(product.getCategory(), CategoryDTO.class);

                // Asignar el objeto BrandDTO al objeto ProductDTO
                productDTO.setBrand(brandDTO);

                // Asignar el objeto CategoryDTO al objeto ProductDTO
                productDTO.setCategory(categoryDTO);

                // Obtener las características asociadas con el producto y convertirlas en objetos FeatureDTO
                Set<FeatureDTO> featureDTOS = new HashSet<>();
                for (Feature feature : product.getFeatures()) {
                    FeatureDTO featureDTO = mapper.convertValue(feature, FeatureDTO.class);
                    featureDTOS.add(featureDTO);
                }

                // Asignar las características al objeto ProductDTO
                productDTO.setFeatures(featureDTOS);

                // Agregar el objeto ProductDTO a la lista de ProductDTOs
                productDTOS.add(productDTO);
            }

            // Devolver la lista de objetos ProductDTO resultantes
            return productDTOS;
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new DatabaseException("Error al listar los productos debido a un problema en la base de datos.", e);
        }
    }

    public List<ProductDTO> searchProductsByName(String keyword) {
        // Llamar a la consulta derivada
        List<Product> products = productRepository.findByNameContaining(keyword);

        // Convertir los resultados a DTOs y devolverlos
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> mapper.convertValue(product, ProductDTO.class))
                .collect(Collectors.toList());

        return productDTOs;
    }
    /*
    public void uploadProductImage(Long productId, MultipartFile file) {

        Optional<Product> product = productRepository.findById(productId);

        if( product.isPresent()) {
            String productImageId = UUID.randomUUID().toString();
            Product realProduct = product.get();
            try {
                s3Service.putObject(
                        s3Buckets.getCustomer(),
                        "product-images/"+productId+"/"+productImageId,
                        file.getBytes()
                );
                ArrayList<String> productPhotos = realProduct.getProductsImageIdArrayList();


                productPhotos.add(productImageId);
                realProduct.setProductImageIdArrayList(productPhotos);
                System.out.println("Real product is: "+realProduct.toString());
                productRepository.save(realProduct);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public byte[] getProductImage(Long productId, int number) throws ResourceNotFoundException {
        System.out.println("getProductImage executed "+number+"ID "+productId);
        ProductDTO product = findProductId(productId);

        System.out.println("productdto is: " + product.toString());

        if (product.getProductsImageIdArrayList().size() > 0){
            String productImageId = product.getOneProductImageIdArrayList(number);


            byte[] brandImage = s3Service.getObject(
                    s3Buckets.getCustomer(),
                    "product-images/"+productId.toString() +"/"+ productImageId
            );

            return brandImage;
        }else{
            throw new ResourceNotFoundException("Image array list is less or equal to 0");
        }

    }

    public List<byte[]> getProductImages(Long productId) throws ResourceNotFoundException {
        ProductDTO product = findProductId(productId);

        String productImageId = product.getOneProductImageIdArrayList(0);


        byte[] brandImage = s3Service.getObject(
                s3Buckets.getCustomer(),
                "product-images/"+productId.toString() +"/"+ productImageId
        );

        List<byte[]> images = new ArrayList<>();
        images.add(brandImage);
        images.add(brandImage);


        return images;
    }

     */
}
