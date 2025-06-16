package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.product.ProductCreatedEvent;
import by.cryptic.springmarket.event.product.ProductDeletedEvent;
import by.cryptic.springmarket.event.product.ProductUpdatedEvent;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.model.read.ProductView;
import by.cryptic.springmarket.repository.read.ProductViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ObjectMapper objectMapper;
    private final ProductViewRepository productViewRepository;
    private final ProductMapper productMapper;

    @KafkaListener(topics = "product-topic", groupId = "product-group")
    public void listenProducts(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        switch (EventType.valueOf(type)) {
            case ProductCreatedEvent -> {
                ProductCreatedEvent event = objectMapper.treeToValue(node, ProductCreatedEvent.class);
                productViewRepository.save(ProductView.builder()
                        .productId(event.getProductId())
                        .name(event.getName())
                        .price(event.getPrice())
                        .quantity(event.getQuantity())
                        .description(event.getDescription())
                        .image(event.getImage())
                        .createdBy(event.getCreatedBy())
                        .categoryId(event.getCategory().id())
                        .build());
            }
            case ProductUpdatedEvent -> {
                ProductUpdatedEvent event = objectMapper.treeToValue(node, ProductUpdatedEvent.class);
                productViewRepository.findById(event.getProductId()).ifPresent(productView -> {
                    productMapper.updateView(productView, event);
                    productViewRepository.save(productView);
                });
            }
            case ProductDeletedEvent -> {
                ProductDeletedEvent event = objectMapper.treeToValue(node, ProductDeletedEvent.class);
                productViewRepository.findById(event.getProductId()).ifPresent(orderView ->
                        productViewRepository.deleteById(event.getProductId()));
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}

