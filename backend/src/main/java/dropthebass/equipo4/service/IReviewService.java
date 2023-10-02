package dropthebass.equipo4.service;

import dropthebass.equipo4.dto.ReviewDTO;
import dropthebass.equipo4.exeptions.BadRequestException;
import dropthebass.equipo4.exeptions.DatabaseException;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IReviewService {

    public ReviewDTO createReview(String username, Long productId, ReviewDTO reviewDTO) throws ResourceNotFoundException, BadRequestException, DatabaseException;
    public List<ReviewDTO> listReviews();
    public Double calculateAverageProductScoring(Long productId) throws ResourceNotFoundException;
    public List<ReviewDTO> getReviewsByProductId(Long productId) throws ResourceNotFoundException;

}
