package by.cryptic.springmarket.service.command;

import java.util.UUID;

public record CategoryUpdateCommand(UUID categoryId, String name, String description) {
}
