package by.cryptic.reviewservice.service.command.it.retry;

import by.cryptic.reviewservice.repository.write.ReviewRepository;
import by.cryptic.reviewservice.service.command.ReviewCreateCommand;
import by.cryptic.reviewservice.service.command.handler.ReviewCreateCommandHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/* Run Docker Desktop before start */
@SpringBootTest
@ActiveProfiles(value = {"test", "jpa"})
@Testcontainers
class ReviewCreateCommandHandlerRetryTest {

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoSpyBean
    private ReviewCreateCommandHandler reviewCreateCommandHandler;

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Test
    void repositoryFalls_thenRetryFallbackAreTriggered() {
        //Arrange
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ReviewCreateCommand reviewCreateCommand = new ReviewCreateCommand(
                "testTitle", "testDesc", 3.4, "testImg", productId, userId
        );

        Mockito.doThrow(new TransientDataAccessResourceException("DB down"))
                .when(reviewRepository).save(any());

        //Act
        assertThrows(Exception.class, () -> reviewCreateCommandHandler.handle(reviewCreateCommand));
        //Assert
        verify(reviewCreateCommandHandler, atLeast(1))
                .reviewRetryFallback(eq(reviewCreateCommand), any(Throwable.class));
        verify(reviewRepository, times(3)).save(any());
    }
}
