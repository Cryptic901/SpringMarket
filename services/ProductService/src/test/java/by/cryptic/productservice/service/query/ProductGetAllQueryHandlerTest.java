package by.cryptic.productservice.service.query;

import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.service.query.handler.ProductGetAllQueryHandler;
import by.cryptic.utils.DTO.ProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@Profile("mongo")
class ProductGetAllQueryHandlerTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ProductGetAllQueryHandler productGetAllQueryHandler;

    @Test
    void getAllProducts_whenProductsExists_shouldReturnAllProducts() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductView productView = ProductView.builder()
                .productId(productId)
                .categoryId(categoryId)
                .name("testProduct")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .price(BigDecimal.TEN)
                .build();
        ProductDTO productDTO = new ProductDTO(productView.getName(),
                productView.getPrice(), productView.getQuantity(),
                productView.getDescription(), productView.getImage(), categoryId);
        SortParamsQuery paramsQuery = new SortParamsQuery(null,
                null, null, null, null, 1,
                10, "name", "desc");
        Mockito.when(mongoTemplate.find(any(Query.class), eq(ProductView.class))).thenReturn(List.of(productView));
        Mockito.when(mongoTemplate.count(any(Query.class), eq(ProductView.class))).thenReturn(1L);
        //Act
        Page<ProductDTO> result = productGetAllQueryHandler.handle(paramsQuery);
        //Assert
        assertNotNull(result);
        assertEquals(productDTO, result.getContent().getFirst());
        Mockito.verify(mongoTemplate, Mockito.times(1))
                .find(any(Query.class), eq(ProductView.class));
        Mockito.verify(mongoTemplate, Mockito.times(1))
                .count(any(Query.class), eq(ProductView.class));
        Mockito.verifyNoMoreInteractions(mongoTemplate);
    }
}