package by.cryptic.springmarket.service.command.handler.category;

import by.cryptic.springmarket.event.category.CategoryDeletedEvent;
import by.cryptic.springmarket.repository.write.CategoryRepository;
import by.cryptic.springmarket.service.command.CategoryDeleteCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryDeleteCommandHandler implements CommandHandler<CategoryDeleteCommand> {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(CategoryDeleteCommand command) {
        categoryRepository.deleteById(command.categoryId());
        eventPublisher.publishEvent(new CategoryDeletedEvent(command.categoryId()));
    }
}
