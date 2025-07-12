package by.cryptic.reviewservice.mapper;

import by.cryptic.reviewservice.model.read.ReviewView;
import by.cryptic.reviewservice.model.write.Review;
import by.cryptic.reviewservice.service.command.ReviewUpdateCommand;
import by.cryptic.reviewservice.service.query.ReviewQuery;
import by.cryptic.utils.event.review.ReviewUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewQuery toQuery(ReviewView review) {
        if (review == null) {
            return null;
        }
        return new ReviewQuery(review.getTitle(), review.getRating(),
                review.getDescription(), review.getImage(),
                review.getCreatedBy(), review.getCreatedAt());
    }

    public void updateEntity(Review review, ReviewUpdateCommand updateDTO) {
        if (review == null || updateDTO == null) return;

        if (updateDTO.reviewId() != null) {
            review.setId(updateDTO.reviewId());
        }
        if (updateDTO.title() != null) {
            review.setTitle(updateDTO.title());
        }

        if (updateDTO.rating() != null) {
            review.setRating(updateDTO.rating());
        }

        if (updateDTO.description() != null) {
            review.setDescription(updateDTO.description());
        }

        if (updateDTO.image() != null) {
            review.setImage(updateDTO.image());
        }

        if (updateDTO.userId() != null) {
            review.setUserId(updateDTO.userId());
        }
    }

    public void updateEvent(ReviewView reviewView, ReviewUpdatedEvent event) {
        if (reviewView == null || event == null) return;

        if (event.getReviewId() != null) {
            reviewView.setReviewId(event.getReviewId());
        }

        if (event.getProductId() != null) {
            reviewView.setProductId(event.getProductId());
        }

        if (event.getTitle() != null) {
            reviewView.setTitle(event.getTitle());
        }

        if (event.getDescription() != null) {
            reviewView.setDescription(event.getDescription());
        }
        if (event.getRating() != null) {
            reviewView.setRating(event.getRating());
        }
        if (event.getImage() != null) {
            reviewView.setImage(event.getImage());
        }
        if (event.getUpdatedBy() != null) {
            reviewView.setUpdatedBy(event.getUpdatedBy());
        }
    }
}
