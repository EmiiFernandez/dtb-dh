package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.FeatureDTO;
import dropthebass.equipo4.entity.Feature;
import dropthebass.equipo4.entity.Product;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.IFeatureRepository;
import dropthebass.equipo4.repository.IProductRepository;
import dropthebass.equipo4.service.IFeatureService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeatureServiceImpl implements IFeatureService {

    private IFeatureRepository featureRepository;

    private IProductRepository productRepository;
    ObjectMapper mapper;
    @Autowired
    public FeatureServiceImpl(IFeatureRepository featureRepository, IProductRepository productRepository, ObjectMapper mapper) {
        this.featureRepository = featureRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }
    /**
     * Crea una nueva característica en la base de datos.
     *
     * @param featureDTO Objeto CategoryDTO que contiene los datos de la nueva característica.
     * @return La característica recién FeatureDTO.
     * @throws DuplicateException Si la característica ya existe en la base de datos.
     * @throws DatabaseException Si ocurre un error al intentar guardar la característica en la base de datos.
     */
    @Override
    public FeatureDTO createFeature(FeatureDTO featureDTO) throws DuplicateException, DatabaseException {
        // Verificar si la característica ya existe por nombre
        Feature existingFeature = featureRepository.findByName(featureDTO.getName());
        if (existingFeature != null) {
            throw new DuplicateException("La característica ya existe.");
        }

        try {
            // Crear una nueva característica y asignar los datos
            Feature newFeature = new Feature();
            newFeature.setName(featureDTO.getName());
            newFeature.setIcon(featureDTO.getIcon());

            // Guardar la nueva característica en la base de datos
            featureRepository.save(newFeature);

            // Devolver el DTO de la característica creada
            return mapper.convertValue(newFeature, FeatureDTO.class);
        } catch (Exception e) {
            throw new DatabaseException("No se pudo crear la característica debido a un error en la base de datos.", e);
        }
    }
    /**
     * Encuentra una característica por su ID.
     *
     * @param id El ID de la característica que se desea encontrar.
     * @return El objeto FeatureDTO que representa la característica encontrada.
     * @throws ResourceNotFoundException Si no se encontró ninguna característica con el ID proporcionado.
     */
    @Override
    public FeatureDTO findFeatureId(Long id) throws ResourceNotFoundException {
        // Buscar la característica por su ID en el repositorio
        Optional<Feature> feature = featureRepository.findFeatureById(id);

        // Verificar si se encontró la característica
        if (feature.isPresent()) {
            // Convertir la entidad Feature a FeatureDTO usando el ObjectMapper
            return mapper.convertValue(feature.get(), FeatureDTO.class);
        } else {
            // Si no se encontró la característica, lanzar una excepción ResourceNotFoundException
            throw new ResourceNotFoundException("No se encontró la característica con el ID:" + id);
        }
    }
    /**
     * Modifica una característica existente en la base de datos con los datos proporcionados en el objeto FeatureDTO.
     *
     * @param id ID de la característica que se desea modificar.
     * @param featureDTO Objeto FeatureDTO que contiene los datos actualizados de la característica.
     * @return La característica modificada en forma de FeatureDTO.
     * @throws ResourceNotFoundException Si no se encontró ninguna característica con el ID proporcionado.
     * @throws DatabaseException Si ocurre un error al intentar guardar los cambios en la base de datos.
     */
    @Override
    public FeatureDTO modifyFeature(Long id, FeatureDTO featureDTO) throws ResourceNotFoundException, DatabaseException {
        // Obtener la característica existente por su ID
        Feature feature = featureRepository.findFeatureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la característica con el ID: " + id));

        // Actualizar el nombre si se proporciona en el DTO
        if (featureDTO.getName() != null) {
            feature.setName(featureDTO.getName());
        }

        // Actualizar el ícono si se proporciona en el DTO
        if (featureDTO.getIcon() != null) {
            feature.setIcon(featureDTO.getIcon());
        }

        try {
            // Guardar los cambios en la base de datos
            featureRepository.save(feature);

            // Convertir y devolver el DTO de la característica modificada
            FeatureDTO modifiedFeatureDTO = mapper.convertValue(feature, FeatureDTO.class);

            // Actualizar la relación con los productos
            Set<Product> productsToUpdate = new HashSet<>();
            for (Product product : feature.getProducts()) {
                if (!product.getFeatures().contains(feature)) {
                    product.addFeature(feature);
                    productsToUpdate.add(product);
                }
            }
            productRepository.saveAll(productsToUpdate);

            // Retornar el DTO de la característica modificada
            return modifiedFeatureDTO;
        } catch (DataAccessException e) {
            // Capturar excepción de error en la base de datos y lanzar DatabaseException
            throw new DatabaseException("Error al modificar la característica en la base de datos", e);
        }
    }
    /**
     * Elimina una característica de la base de datos por su ID.
     *
     * @param id ID de la característica que se desea eliminar.
     * @throws ResourceNotFoundException Si no se encontró ninguna característica con el ID proporcionado.
     * @throws DatabaseException Si la característica tiene productos asociados o hay un error al eliminarla de la base de datos.
     */
    @Override
    public void deleteFeature(Long id) throws ResourceNotFoundException {
        // Buscar la característica por su ID en el repositorio
        Feature feature = featureRepository.findFeatureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la característica con el ID: " + id));

        try {
            // Verificar si la característica tiene productos asociados
            if (!feature.getProducts().isEmpty()) {
                throw new DatabaseException("No se puede eliminar la característica debido a que tiene productos asociados.");
            }

            // Eliminar la característica desde el repositorio
            featureRepository.delete(feature);
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new DatabaseException("No se pudo eliminar la característica debido a un error en la base de datos.", e);
        }
    }
    /**
     * Lista todas las características almacenadas en la base de datos.
     *
     * @return Una lista de objetos FeatureDTO que representan las características.
     * @throws RuntimeException Si no se pudo listar las características debido a un error en la base de datos.
     */
    @Override
    public List<FeatureDTO> listFeatures() {
        try {
            // Obtener todas las características del repositorio
            List<Feature> features = featureRepository.findAll();
            List<FeatureDTO> featureDTOS = new ArrayList<>();

            // Convertir cada entidad de características en un DTO y agregarlo a la lista resultante
            for (Feature feature : features) {
                FeatureDTO featureDTO = mapper.convertValue(feature, FeatureDTO.class);

                featureDTO.setProducts(feature.getProducts());

                featureDTOS.add(featureDTO);
            }
            // Devolver la lista de DTOs de características
            return featureDTOS;
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new RuntimeException("Error al intentar listar las características debido a un problema en la base de datos.", e);
        }
    }

    @Transactional
    public Feature getOrCreateFeature (FeatureDTO featureDTO) throws ResourceNotFoundException {
        if (featureDTO.getId() != null) {
            // Se busca por ID
            return featureRepository.findById(featureDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));
        } else {
            //Si no agrego el ID, se intenta buscar por nombre
            Feature feature = featureRepository.findByName(featureDTO.getName());

            if (feature == null) {
                //Si no existe, se crea nueva característica
                feature = new Feature(featureDTO.getName(), featureDTO.getIcon());
                featureRepository.save(feature);
            }
            return feature;
        }
    }
}
