package dropthebass.equipo4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dropthebass.equipo4.dto.CategoryDTO;
import dropthebass.equipo4.entity.Category;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;
import dropthebass.equipo4.repository.ICategoryRepository;
import dropthebass.equipo4.service.ICategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    ObjectMapper mapper;

    // Guarda una categoría en la base de datos a partir de un objeto CategoryDTO
    private void saveCategory(CategoryDTO categoryDTO) {
        Category category = mapper.convertValue(categoryDTO, Category.class);
        categoryRepository.save(category);
    }


    /**
     * Crea una nueva categoría en la base de datos.
     *
     * @param categoryDTO Objeto CategoryDTO que contiene los datos de la nueva categoría.
     * @return La categoría recién creada.
     * @throws DuplicateException Si la categoría ya existe en la base de datos.
     * @throws DatabaseException Si ocurre un error al intentar guardar la categoría en la base de datos.
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws DuplicateException, DatabaseException {
        // Verificar si la categoría ya existe por nombre
        Category existingCategory = categoryRepository.findByName(categoryDTO.getName());
        if (existingCategory != null) {
            throw new DuplicateException("La categoría ya existe.");
        }

        try {
            // Crear una nueva categoría y asignar los datos
            Category newCategory = new Category();
            newCategory.setName(categoryDTO.getName());

            // Guardar la nueva categoría en la base de datos
            categoryRepository.save(newCategory);

            // Devolver el DTO de la categoría creada
            return mapper.convertValue(newCategory, CategoryDTO.class);
        } catch (Exception e) {
            throw new DatabaseException("No se pudo crear la categoría debido a un error en la base de datos.", e);
        }
    }


    /**
     * Encuentra una categoría por su ID.
     *
     * @param id El ID de la categoría que se desea encontrar.
     * @return El objeto CategoryDTO que representa la categoría encontrada.
     * @throws ResourceNotFoundException Si no se encontró ninguna categoría con el ID proporcionado.
     */
    @Override
    public CategoryDTO findCategoryId(Long id) throws ResourceNotFoundException {
        // Buscar la categoría por su ID en el repositorio
        Optional<Category> category = categoryRepository.findById(id);
        // Verificar si se encontró la categoría
        if (category.isPresent()) {
            // Convertir la entidad Category a CategoryDTO usando el ObjectMapper
            return mapper.convertValue(category.get(), CategoryDTO.class);
        } else {
            // Si no se encontró la categoría, lanzar una excepción ResourceNotFoundException
            throw new ResourceNotFoundException("No se encontró la categoría con el ID: " + id);
        }
    }
    /**
     * Modifica una categoría existente en la base de datos con los datos proporcionados en el objeto CategoryDTO.
     *
     * @param id ID de la categoría que se desea modificar.
     * @param categoryDTO Objeto CategoryDTO que contiene los datos actualizados de la categoría.
     * @return La categoría modificada.
     * @throws ResourceNotFoundException Si no se encontró ninguna categoría con el ID proporcionado.
     * @throws DatabaseException Si ocurre un error al intentar guardar los cambios en la base de datos.
     */
    @Override
    public CategoryDTO modifyCategory(Long id, CategoryDTO categoryDTO) throws ResourceNotFoundException, DatabaseException {
        // Obtener la categoría existente por su ID
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con el ID: " + id));

        // Actualizar los atributos si están presentes en el CategoryDTO
        if (categoryDTO.getName() != null) {
            category.setName(categoryDTO.getName());
        }

        try {
            // Guardar los cambios en la base de datos
            categoryRepository.save(category);
            // Devolver el DTO de la categoría modificada
            return mapper.convertValue(category, CategoryDTO.class);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al modificar la categoría en la base de datos", e);
        }
    }
    /**
     * Elimina una categoría de la base de datos por su ID.
     *
     * @param id ID de la categoría que se desea eliminar.
     * @throws ResourceNotFoundException Si no se encontró ninguna categoría con el ID proporcionado.
     * @throws DatabaseException         Si la categoría tiene productos asociados o hay un error al eliminarla de la base de datos.
     */
    @Override
    public void deleteCategory(Long id) throws ResourceNotFoundException {
        // Buscar la categoría por su ID en el repositorio
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con el ID: " + id));

        // Verificar si la categoría tiene productos asociados
        if (!category.getProducts().isEmpty()) {
            // Si tiene productos asociados, lanzar una excepción DatabaseException
            throw new DatabaseException("No se puede eliminar la categoría debido a que tiene productos asociados.");
        }

        try {
            // Eliminar la categoría desde el repositorio
            categoryRepository.delete(category);
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new RuntimeException("No se pudo eliminar la categoría.", e);
        }
    }

    /**
     * Lista todas las categorías almacenadas en la base de datos.
     *
     * @return Una lista de objetos CategoryDTO que representan las categorías.
     * @throws RuntimeException Si no se pudo listar las categorías debido a un error en la base de datos.
     */
    @Override
    public List<CategoryDTO> listCategories() {
        try {
            // Obtener todas las categorías del repositorio
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDTO> categoryDTOS = new ArrayList<>();

            // Convertir cada entidad de categoría en un DTO y agregarlo a la lista resultante
            for (Category category : categories) {
                CategoryDTO categoryDTO = mapper.convertValue(category, CategoryDTO.class);
                categoryDTOS.add(categoryDTO);
            }

            // Devolver la lista de DTOs de categorías
            return categoryDTOS;
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a la base de datos
            throw new RuntimeException("Error al intentar listar las categorías debido a un problema en la base de datos.", e);
        }
    }
    /**
     * Busca una categoría por su ID o nombre. Si no existe, crea una nueva categoría.
     *
     * @param categoryDTO Objeto CategoryDTO que contiene los datos de la categoría a buscar o crear.
     * @return La categoría encontrada o creada.
     * @throws ResourceNotFoundException Si no se encontró la categoría por ID o por nombre.
     */
    @Transactional
    public Category getOrCreateCategory (CategoryDTO categoryDTO) throws ResourceNotFoundException {
        if (categoryDTO.getId() != null) {
            // Se busca por ID
            return categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        } else {
            //Si no agrego el ID, se intenta buscar por nombre
            Category category = categoryRepository.findByName(categoryDTO.getName());

            if (category == null) {
                //Si no existe, se crea nueva categoría
                category = new Category(categoryDTO.getName(), new HashSet<>());
                categoryRepository.save(category);
            }
            return category;
        }
    }
}