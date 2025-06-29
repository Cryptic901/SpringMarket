package by.cryptic.reviewservice.service.command;

import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public record ReviewDeleteCommand(UUID reviewId, UUID userId) {
}
