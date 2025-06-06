package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullReviewDTO;
import by.cryptic.springmarket.model.read.ReviewView;
import by.cryptic.springmarket.model.write.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullReviewMapper {

    Review toEntity(FullReviewDTO fullReviewDTO);

    FullReviewDTO toDto(Review review);
    FullReviewDTO toDto(ReviewView review);
}
