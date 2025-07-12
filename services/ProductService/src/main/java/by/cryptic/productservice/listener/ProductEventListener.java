package by.cryptic.productservice.listener;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;
    private final ProductViewRepository productViewRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @KafkaListener(topics = {"product-topic", "order-topic"}, groupId = "product-group")
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
                        .categoryId(event.getCategoryId())
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
                productViewRepository.findById(event.getProductId()).ifPresent(_ ->
                        productViewRepository.deleteById(event.getProductId()));
            }
            case OrderCreatedEvent -> {
                OrderSuccessEvent event = objectMapper.treeToValue(node, OrderSuccessEvent.class);
                List<OrderedProductDTO> orderedProductDTOS = event.getListOfProducts();

                Map<UUID, OrderedProductDTO> orderedMap = orderedProductDTOS.stream()
                        .collect(Collectors.toMap(OrderedProductDTO::productId, dto -> dto));

                List<Product> productsToUpdate = productRepository.findAllById(orderedMap.keySet());

                for (Product product : productsToUpdate) {
                    OrderedProductDTO dto = orderedMap.get(product.getId());
                    productMapper.updateEntity(product, dto);
                }
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}

