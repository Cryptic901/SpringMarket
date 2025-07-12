package by.cryptic.reviewservice.mapper;

import by.cryptic.utils.DTO.ReviewDTO;
import by.cryptic.reviewservice.model.read.ReviewView;
import org.springframework.stereotype.Component;

@Component
public class FullReviewMapper {

   public ReviewDTO toDto(ReviewView review) {
       if (review == null) {
           return null;
       }
       return ReviewDTO.builder()
               .productId(review.getProductId())
               .title(review.getTitle())
               .rating(review.getRating())
               .description(review.getDescription())
               .image(review.getImage())
               .createdAt(review.getCreatedAt())
               .updatedAt(review.getUpdatedAt())
               .createdBy(review.getCreatedBy())
               .createdAt(review.getCreatedAt())
               .build();
   }
}
