package by.cryptic.productservice.service.command.category;

import java.util.UUID;

public record CategoryUpdateCommand(UUID categoryId, String name, String description) {
}
