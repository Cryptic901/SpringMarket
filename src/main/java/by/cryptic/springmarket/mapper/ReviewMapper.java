package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.service.query.ReviewDTO;
import by.cryptic.springmarket.event.review.ReviewUpdatedEvent;
import by.cryptic.springmarket.model.read.ReviewView;
import by.cryptic.springmarket.service.command.ReviewUpdateCommand;
import by.cryptic.springmarket.model.write.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    ReviewDTO toDto(Review review);
    ReviewDTO toDto(ReviewView review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Review review, ReviewUpdateCommand updateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(@MappingTarget ReviewView reviewView, ReviewUpdatedEvent event);
}
