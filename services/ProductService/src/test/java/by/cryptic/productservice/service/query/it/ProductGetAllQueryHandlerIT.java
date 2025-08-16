package by.cryptic.productservice.service.query.it;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.service.query.SortParamsQuery;
import by.cryptic.productservice.service.query.handler.ProductGetAllQueryHandler;
import by.cryptic.utils.DTO.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/* Run Docker Desktop before start */
@SpringBootTest
@Transactional
@Testcontainers
@Import(ProductMapper.class)
@ActiveProfiles(value = {"test", "mongo"})
public class ProductGetAllQueryHandlerIT {

    @Autowired
    MongoTemplate mongoTemplate;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4");

    @Autowired
    ProductGetAllQueryHandler productGetAllQueryHandler;

    @DynamicPropertySource
    static void propertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getConnectionString() + "/test_db");
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @BeforeEach
    void cleanDB() {
        mongoTemplate.dropCollection(ProductView.class);
    }

    @Test
    void getAllProducts_withSorting_shouldReturnAllProductsSorted() {
        //Arrange
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductView productViewA = ProductView.builder()
                .productId(productId1)
                .categoryId(categoryId)
                .name("aaaaa")
                .description("testDesc")
                .image("test/img")
                .quantity(41)
                .price(BigDecimal.TEN)
                .build();
        ProductView productViewB = ProductView.builder()
                .productId(productId2)
                .categoryId(categoryId)
                .name("bbbbb")
                .description("testDesc")
                .image("test/img")
                .quantity(33)
                .price(BigDecimal.TEN)
                .build();
        ProductView productViewC = ProductView.builder()
                .productId(productId3)
                .categoryId(categoryId)
                .name("ccccc")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .price(BigDecimal.TEN)
                .build();

        mongoTemplate.save(productViewA);
        mongoTemplate.save(productViewB);
        mongoTemplate.save(productViewC);
        SortParamsQuery paramsQuery = new SortParamsQuery(null,
                null, null, null, null, 1,
                10, "quantity", "desc");
        //Act
        Page<ProductDTO> result = productGetAllQueryHandler.handle(paramsQuery);
        //Assert
        System.out.println(result.getContent());
        List<ProductDTO> content = result.getContent();
        assertNotNull(result);
        assertEquals(3, content.size());
        assertEquals("ccccc", content.getFirst().name());
        assertEquals("aaaaa", content.get(1).name());
        assertEquals("bbbbb", content.get(2).name());
    }

    @Test
    void getAllProducts_withPriceInRange_shouldReturnAllProductsWithPriceInRange() {
        //Arrange
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductView productViewA = ProductView.builder()
                .productId(productId1)
                .categoryId(categoryId)
                .name("aaaaa")
                .description("testDesc")
                .image("test/img")
                .quantity(41)
                .price(BigDecimal.valueOf(2.0))
                .build();
        ProductView productViewB = ProductView.builder()
                .productId(productId2)
                .categoryId(categoryId)
                .name("bbbbb")
                .description("testDesc")
                .image("test/img")
                .quantity(33)
                .price(BigDecimal.valueOf(1.0))
                .build();
        ProductView productViewC = ProductView.builder()
                .productId(productId3)
                .categoryId(categoryId)
                .name("ccccc")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .price(BigDecimal.valueOf(10.0))
                .build();

        mongoTemplate.save(productViewA);
        mongoTemplate.save(productViewB);
        mongoTemplate.save(productViewC);
        SortParamsQuery paramsQuery = new SortParamsQuery(null,
                null, null, BigDecimal.valueOf(2.0), BigDecimal.valueOf(10.0), 1,
                10, "quantity", "desc");
        //Act
        Page<ProductDTO> result = productGetAllQueryHandler.handle(paramsQuery);
        //Assert
        List<ProductDTO> content = result.getContent();
        assertNotNull(result);
        assertEquals(2, content.size());
        content.forEach(product -> assertNotEquals(BigDecimal.valueOf(1.0), product.price()));

    }
}
