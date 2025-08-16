//package by.cryptic.orderservice.controller.command;
//
//import by.cryptic.productservice.dto.ProductCreateDTO;
//import by.cryptic.productservice.dto.ProductUpdateDTO;
//import by.cryptic.productservice.model.write.Product;
//import by.cryptic.productservice.service.command.product.handler.ProductCreateCommandHandler;
//import by.cryptic.productservice.service.command.product.handler.ProductDeleteCommandHandler;
//import by.cryptic.productservice.service.command.product.handler.ProductUpdateCommandHandler;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc(printOnlyOnFailure = false)
//@WebMvcTest(ProductCommandController.class)
//@ActiveProfiles("jpa")
//class ProductCommandControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private ProductCreateCommandHandler productCreateCommandHandler;
//
//    @MockitoBean
//    private ProductUpdateCommandHandler productUpdateCommandHandler;
//
//    @MockitoBean
//    private ProductDeleteCommandHandler productDeleteCommandHandler;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser
//    void createReview_withAuthorizedUser_shouldCreateReview() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        Product product = Product.builder()
//                .id(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
//                product.getName(),
//                product.getPrice(),
//                product.getQuantity(),
//                product.getDescription(),
//                product.getImage(),
//                categoryId
//        );
//        String json = objectMapper.writeValueAsString(productCreateDTO);
//        //Act
//        mockMvc.perform(post("/api/v1/products")
//                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isCreated());
//        //Assert
//        verify(productCreateCommandHandler, times(1)).handle(any());
//        verifyNoMoreInteractions(productCreateCommandHandler);
//    }
//
//    @Test
//    void createReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        Product product = Product.builder()
//                .id(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
//                product.getName(),
//                product.getPrice(),
//                product.getQuantity(),
//                product.getDescription(),
//                product.getImage(),
//                categoryId
//        );
//        String json = objectMapper.writeValueAsString(productCreateDTO);
//        //Act
//        mockMvc.perform(post("/api/v1/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isForbidden());
//        //Assert
//        verifyNoMoreInteractions(productCreateCommandHandler);
//    }
//
//    @Test
//    @WithMockUser
//    void updateReview_withAuthorizedUser_shouldUpdateReview() throws Exception {
//        //Arrange
//        UUID productId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
//                "newTitle", null, null, null, null, categoryId);
//        String json = objectMapper.writeValueAsString(productUpdateDTO);
//        //Act
//        mockMvc.perform(patch("/api/v1/products/" + productId)
//                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().is2xxSuccessful());
//        //Assert
//        verify(productUpdateCommandHandler, times(1)).handle(any());
//        verifyNoMoreInteractions(productUpdateCommandHandler);
//    }
//
//    @Test
//    void updateReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
//        //Arrange
//        UUID productId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
//                "newTitle", null, null, null, null, categoryId);
//        String json = objectMapper.writeValueAsString(productUpdateDTO);
//        //Act
//        mockMvc.perform(patch("/api/v1/products/" + productId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isForbidden());
//        //Assert
//        verifyNoMoreInteractions(productUpdateCommandHandler);
//    }
//
//    @Test
//    void deleteReview_withAuthorizedUser_shouldDeleteReview() throws Exception {
//        //Arrange
//        //Act
//        mockMvc.perform(delete("/api/v1/products/" + UUID.randomUUID())
//                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful());
//        //Assert
//        verify(productDeleteCommandHandler, times(1)).handle(any());
//        verifyNoMoreInteractions(productDeleteCommandHandler);
//    }
//
//    @Test
//    void deleteReview_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
//        //Arrange
//        //Act
//        mockMvc.perform(delete("/api/v1/products/" + UUID.randomUUID())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//        //Assert
//        verifyNoMoreInteractions(productDeleteCommandHandler);
//    }
//}