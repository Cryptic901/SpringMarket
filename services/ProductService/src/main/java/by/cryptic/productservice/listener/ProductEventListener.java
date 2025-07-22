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
    private final ProductMapper productMapper;

    @KafkaListener(topics = {"product-topic", "order-topic"}, groupId = "product-group")
    public void listenProducts(DomainEvent event) {
        log.debug("Receive product event {}", event);
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
                    productViewRepository.findById(productUpdatedEvent.getProductId()).ifPresent(productView -> {
                        productMapper.updateView(productView, productUpdatedEvent);
                        productViewRepository.save(productView);
                    });

            case ProductDeletedEvent productDeletedEvent ->
                    productViewRepository.findById(productDeletedEvent.getProductId()).ifPresent(_ ->
                            productViewRepository.deleteById(productDeletedEvent.getProductId()));

            case OrderCreatedEvent orderCreatedEvent -> {
                List<OrderedProductDTO> orderedProductDTOS = orderCreatedEvent.getListOfProducts();

                Map<UUID, OrderedProductDTO> orderedMap = orderedProductDTOS.stream()
                        .collect(Collectors.toMap(OrderedProductDTO::productId, dto -> dto));

                List<Product> productsToUpdate = productRepository.findAllById(orderedMap.keySet());

                for (Product product : productsToUpdate) {
                    OrderedProductDTO dto = orderedMap.get(product.getId());
                    productMapper.updateEntity(product, dto);
                }
            }
            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}
