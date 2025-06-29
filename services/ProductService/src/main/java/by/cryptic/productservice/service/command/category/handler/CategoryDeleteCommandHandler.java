package by.cryptic.productservice.service.command.category.handler;

import by.cryptic.utils.event.category.CategoryDeletedEvent;
import by.cryptic.productservice.repository.write.CategoryRepository;
import by.cryptic.productservice.service.command.category.CategoryDeleteCommand;
import by.cryptic.utils.CommandHandler;
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
