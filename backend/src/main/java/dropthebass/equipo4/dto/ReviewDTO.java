package dropthebass.equipo4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private String userFullName;
    private Integer productScoring;
    private Date reviewDate;
    private String comments;
    private Long productId;

}
