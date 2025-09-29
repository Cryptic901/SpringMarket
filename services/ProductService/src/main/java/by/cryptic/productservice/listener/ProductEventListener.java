package by.cryptic.productservice.listener;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import by.cryptic.utils.event.product.ProductUpdatedQuantityFromStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ProductViewRepository productViewRepository;
    private final ProductRepository productRepository;

    @KafkaListener(topics = {"product-topic", "order-topic"})
    public void listenProducts(DomainEvent event) {
        log.debug("Received event: {}", event.getClass().getSimpleName());
        switch (event) {
            case ProductCreatedEvent productCreatedEvent -> productViewRepository.save(ProductView.builder()
                    .productId(productCreatedEvent.getProductId())
                    .name(productCreatedEvent.getName())
                    .price(productCreatedEvent.getPrice())
                    .quantity(productCreatedEvent.getQuantity())
                    .description(productCreatedEvent.getDescription())
                    .image(productCreatedEvent.getImage())
                    .createdBy(productCreatedEvent.getCreatedBy())
                    .categoryId(productCreatedEvent.getCategoryId())
                    .build());

            case ProductUpdatedEvent productUpdatedEvent ->
                    productViewRepository.findById(productUpdatedEvent.getProductId())
                            .ifPresent(productView -> {
                                ProductMapper.updateView(productView, productUpdatedEvent);
                                productViewRepository.save(productView);
                            });

            case ProductDeletedEvent productDeletedEvent ->
                    productViewRepository.findById(productDeletedEvent.getProductId())
                            .ifPresent(productView ->
                                    productViewRepository.deleteById(productDeletedEvent.getProductId()));

            case OrderCreatedEvent orderCreatedEvent -> {
                List<OrderedProductDTO> orderedProductDTOS = orderCreatedEvent.getListOfProducts();
                log.info("Received products {}", orderedProductDTOS);
                Map<UUID, OrderedProductDTO> orderedMap = orderedProductDTOS.stream()
                        .collect(Collectors.toMap(OrderedProductDTO::productId, dto -> dto));
                log.info("Ordered products map {}", orderedMap);
                List<Product> productsToUpdate = productRepository.findAllById(orderedMap.keySet());

                log.info("Products to update {}", productsToUpdate);
                for (Product product : productsToUpdate) {
                    OrderedProductDTO dto = orderedMap.get(product.getId());
                    product.setQuantity(product.getQuantity() - dto.quantity());
                    log.info("Updated product {}", dto);
                }
                productRepository.saveAll(productsToUpdate);
            }

            default -> log.warn("Unexpected event type: {}", event.getClass().getSimpleName());
        }
    }

    @KafkaListener(topics = "inventory-topic")
    public void listenDebezium(DomainEvent event) {
        log.info("-------------------------------------------");
        log.info("SAGA LISTENING IN PRODUCT EVENT LISTENER");
        log.info("!!!!!Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case ProductUpdatedQuantityFromStockEvent productUpdatedQuantityFromStockEvent -> {
                Product product = productRepository.findById
                                (productUpdatedQuantityFromStockEvent.getProductId())
                        .orElseThrow(() -> new IllegalStateException("There are no products to update quantity"));

                ProductView productView = productViewRepository.findById
                                (productUpdatedQuantityFromStockEvent.getProductId())
                        .orElseThrow(() -> new IllegalStateException("There are no products to update quantity"));
                log.info("product updated quantity from saga: {}", productUpdatedQuantityFromStockEvent.getQuantity());
                log.info("product quantity : {}", product.getQuantity());
                product.setQuantity(productUpdatedQuantityFromStockEvent.getQuantity());

                log.info("product updated quantity : {}", product.getQuantity());

                productView.setQuantity(productUpdatedQuantityFromStockEvent.getQuantity());

                productRepository.save(product);
                log.info("SAGA PRODUCT UPDATED PRODUCT QUANTITY: {}", product);
                productViewRepository.save(productView);
                log.info("SAGA PRODUCT VIEW UPDATED PRODUCT QUANTITY: {}", productView);
            }
            default -> log.warn("Unexpected event type: {}", event.getClass().getSimpleName());
        }
    }
}
