package by.cryptic.reviewservice.mapper;

import by.cryptic.reviewservice.dto.ReviewDTO;
import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.model.write.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullReviewMapper {


    ReviewDTO toDto(Review review);
    ReviewDTO toDto(ReviewView review);
}
