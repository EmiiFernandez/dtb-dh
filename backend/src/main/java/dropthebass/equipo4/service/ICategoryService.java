package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.CategoryDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface ICategoryService {
    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws DuplicateException, DatabaseException;
    public CategoryDTO findCategoryId (Long id) throws ResourceNotFoundException;
    public CategoryDTO modifyCategory(Long id, CategoryDTO brandDTO)  throws ResourceNotFoundException, DatabaseException;
    public void deleteCategory (Long id) throws ResourceNotFoundException;
    public List<CategoryDTO> listCategories();
}
