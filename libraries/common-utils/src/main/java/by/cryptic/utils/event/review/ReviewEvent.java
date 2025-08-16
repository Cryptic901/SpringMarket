package by.cryptic.utils.event.review;

import java.util.UUID;

@FunctionalInterface
public interface ReviewEvent {
    UUID getReviewId();
}
