package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.ReviewDTO;
import by.cryptic.springmarket.dto.UpdateReviewDTO;
import by.cryptic.springmarket.model.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toEntity(ReviewDTO reviewDTO);

    ReviewDTO toDto(Review review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Review review, UpdateReviewDTO updateDTO);
}
