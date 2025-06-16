package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.model.read.ReviewView;
import by.cryptic.springmarket.model.write.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullReviewMapper {


    ReviewDTO toDto(Review review);
    ReviewDTO toDto(ReviewView review);
}
