//package by.cryptic.orderservice.controller.query;
//
//import by.cryptic.productservice.mapper.ProductMapper;
//import by.cryptic.productservice.model.read.ProductView;
//import by.cryptic.productservice.service.query.product.handler.ProductGetAllQueryHandler;
//import by.cryptic.productservice.service.query.product.handler.ProductGetByIdQueryHandler;
//import by.cryptic.utils.DTO.ProductDTO;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc(printOnlyOnFailure = false)
//@WebMvcTest(ProductQueryController.class)
//@ActiveProfiles("mongo")
//@Import(ProductMapper.class)
//class ProductQueryControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ProductMapper productMapper;
//
//    @MockitoBean
//    private ProductGetAllQueryHandler productGetAllQueryHandler;
//
//    @MockitoBean
//    private ProductGetByIdQueryHandler productGetByIdQueryHandler;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser
//    void getAllReviews_withProductThatExists_shouldReturnAllReviews() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        ProductView productView = ProductView.builder()
//                .productId(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        productDTOS.add(productMapper.toDto(productView));
//        Page<ProductDTO> page = new PageImpl<>(productDTOS);
//        Mockito.when(productGetAllQueryHandler.handle(any())).thenReturn(page);
//        //Act
//        var mvc = mockMvc.perform(get("/api/v1/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(page)));
//        //Assert
//        String result = mvc.andReturn().getResponse().getContentAsString();
//        Assertions.assertEquals(objectMapper.writeValueAsString(page), result);
//        Mockito.verify(productGetAllQueryHandler, Mockito.times(1)).handle(any());
//        Mockito.verifyNoMoreInteractions(productGetAllQueryHandler);
//    }
//
//    @Test
//    void getAllReviews_withNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        ProductView productView = ProductView.builder()
//                .productId(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        productDTOS.add(productMapper.toDto(productView));
//        Page<ProductDTO> page = new PageImpl<>(productDTOS);
//        Mockito.when(productGetAllQueryHandler.handle(any())).thenReturn(page);
//        //Act
//        //Assert
//        mockMvc.perform(get("/api/v1/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//
//    }
//
//    @Test
//    void getAllReviews_withAdminRole_shouldReturnAllReviews() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        ProductView productView = ProductView.builder()
//                .productId(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        productDTOS.add(productMapper.toDto(productView));
//        Page<ProductDTO> page = new PageImpl<>(productDTOS);
//        Mockito.when(productGetAllQueryHandler.handle(any())).thenReturn(page);
//        //Act
//        mockMvc.perform(get("/api/v1/products")
//                        .with(jwt().jwt(jwt -> {
//                            jwt.claim("sub", UUID.randomUUID());
//                            jwt.claim("realm_access.roles", "ROLE_ADMIN");
//                        })))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(page)));
//        //Assert
//        Mockito.verify(productGetAllQueryHandler, times(1)).handle(any());
//        Mockito.verifyNoMoreInteractions(productGetAllQueryHandler);
//    }
//
//    @Test
//    @WithMockUser
//    void getReviewById_withProductThatExistsAndAuthorizedUser_shouldReturnReview() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        ProductView productView = ProductView.builder()
//                .productId(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductDTO productDTO = productMapper.toDto(productView);
//        Mockito.when(productGetByIdQueryHandler.handle(productId)).thenReturn(productDTO);
//        //Act
//        mockMvc.perform(get("/api/v1/products/" + productId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(productDTO)));
//        //Assert
//        Mockito.verify(productGetByIdQueryHandler, times(1)).handle(productId);
//        Mockito.verifyNoMoreInteractions(productGetByIdQueryHandler);
//    }
//
//    @Test
//    void getReviewById_withProductThatExistsAndAdminRole_shouldReturnReview() throws Exception {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        ProductView productView = ProductView.builder()
//                .productId(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductDTO productDTO = productMapper.toDto(productView);
//        Mockito.when(productGetByIdQueryHandler.handle(productId)).thenReturn(productDTO);
//        //Act
//        mockMvc.perform(get("/api/v1/products/" + productId)
//                        .with(jwt().jwt(jwt -> {
//                            jwt.claim("sub", UUID.randomUUID());
//                            jwt.claim("realm_access.roles", "ROLE_ADMIN");
//                        })))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(productDTO)));
//        //Assert
//        Mockito.verify(productGetByIdQueryHandler, times(1)).handle(productId);
//        Mockito.verifyNoMoreInteractions(productGetByIdQueryHandler);
//    }
//
//    @Test
//    void getReviewById_withProductThatExistsAndNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
//        //Arrange
//        //Act
//        //Assert
//        mockMvc.perform(get("/api/v1/products/" + UUID.randomUUID()))
//                .andExpect(status().isUnauthorized());
//    }
//}