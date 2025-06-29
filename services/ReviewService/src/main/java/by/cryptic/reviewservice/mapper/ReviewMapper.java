package by.cryptic.reviewservice.mapper;

import by.cryptic.reviewservice.dto.ReviewDTO;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.reviewservice.service.query.ReviewQuery;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    ReviewDTO toDto(Review review);
    ReviewQuery toQuery(ReviewView review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Review review, ReviewUpdateCommand updateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(@MappingTarget ReviewView reviewView, ReviewUpdatedEvent event);
}
