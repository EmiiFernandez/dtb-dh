package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.FeatureDTO;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.DuplicateException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IFeatureService {
    public FeatureDTO createFeature(FeatureDTO featureDTO) throws DuplicateException, DatabaseException;
    public FeatureDTO findFeatureId (Long id) throws ResourceNotFoundException;
    public FeatureDTO modifyFeature (Long id, FeatureDTO featureDTO) throws ResourceNotFoundException, DatabaseException ;
    public void deleteFeature (Long id) throws ResourceNotFoundException;
    public List<FeatureDTO> listFeatures();
}
