package by.cryptic.cartservice.controller.query;

import by.cryptic.cartservice.service.query.handler.CartGetAllQueryHandler;
import by.cryptic.cartservice.service.query.handler.CartGetByIdQueryHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(CartQueryController.class)
@ActiveProfiles("mongo")
class CartQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartGetAllQueryHandler cartGetAllQueryHandler;

    @MockitoBean
    private CartGetByIdQueryHandler cartGetByIdQueryHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllCarts_withCartThatExists_shouldReturnAllCarts() throws Exception {
        //Arrange
        List<CartProductDTO> cartDTOS = new ArrayList<>();
        cartDTOS.add(new CartProductDTO());
        Mockito.when(cartGetAllQueryHandler.handle(any())).thenReturn(cartDTOS);
        //Act
        mockMvc.perform(get("/api/v1/carts")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(cartDTOS)));
        //Assert
        Mockito.verify(cartGetAllQueryHandler, times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(cartGetAllQueryHandler);
    }

    @Test
    void getAllCarts_withNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        List<CartProductDTO> cartDTOS = new ArrayList<>();
        cartDTOS.add(new CartProductDTO());
        Mockito.when(cartGetAllQueryHandler.handle(any())).thenReturn(cartDTOS);
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getCartById_withCartThatExists_shouldReturnCart() throws Exception {
        //Arrange
        UUID productId = UUID.randomUUID();
        CartProductDTO cartProductDTO = new CartProductDTO(productId, 45, BigDecimal.ONE);
        Mockito.when(cartGetByIdQueryHandler.handle(any())).thenReturn(cartProductDTO);
        //Act
        mockMvc.perform(get("/api/v1/carts/{productId}", productId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID()))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cartProductDTO)));
        //Assert
        Mockito.verify(cartGetByIdQueryHandler, times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(cartGetByIdQueryHandler);
    }

    @Test
    void getCartById_withCartThatExistsAndNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/categories/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}