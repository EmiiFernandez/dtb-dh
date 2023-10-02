package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.FavsDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IFavsService {
    public FavsDTO createFavs(FavsDTO favsDTO) throws DuplicateException, DatabaseException, ResourceNotFoundException;
    public FavsDTO deleteFavs(Long productId) throws ResourceNotFoundException;
    public List<FavsDTO> listFavs();
}
