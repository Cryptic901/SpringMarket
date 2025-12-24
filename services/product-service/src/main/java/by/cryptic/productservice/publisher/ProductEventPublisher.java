package by.cryptic.productservice.publisher;

import by.cryptic.productservice.model.write.OutboxEntity;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.OutboxRepository;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final OutboxRepository outboxRepository;

    public void saveProductView(Product product) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductCreatedEvent))
                .payload(ProductCreatedEvent.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .quantity(product.getQuantity())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .categoryId(product.getCategoryId())
                        .createdBy(product.getCreatedBy())
                        .productStatus(product.getProductStatus())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void deleteProductAndView(Product product) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductDeletedEvent))
                .payload(ProductDeletedEvent.builder()
                        .productId(product.getId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void updateProductView(Product product, ProductUpdateCommand updateProductDTO) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductUpdatedEvent))
                .payload(ProductUpdatedEvent.builder()
                        .productId(updateProductDTO.productId())
                        .name(updateProductDTO.name())
                        .image(updateProductDTO.image())
                        .description(updateProductDTO.description())
                        .quantity(updateProductDTO.quantity())
                        .price(updateProductDTO.price())
                        .categoryId(product.getCategoryId())
                        .productStatus(updateProductDTO.productStatus())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }
}
