package by.cryptic.cartservice.controller.command;

import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartClearCommandHandler;
import by.cryptic.cartservice.service.command.handler.CartDeleteProductCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(CartCommandController.class)
@ActiveProfiles(value = {"jpa", "security", "test"})
class CartCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CartAddCommandHandler cartAddCommandHandler;

    @MockitoBean
    private CartDeleteProductCommandHandler cartDeleteProductCommandHandler;

    @MockitoBean
    private CartClearCommandHandler cartClearCommandHandler;


    @Test
    void addProductToCart_withAuthorizedUser_shouldAddProductToCart() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        //Act
        mockMvc.perform(post("/api/v1/carts/{productId}", productId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", userId)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(cartAddCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(cartAddCommandHandler);
    }

    @Test
    void addProductToCart_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(post("/api/v1/carts/{productId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(cartAddCommandHandler);
    }

    @Test
    void cartClear_withAuthorizedUser_shouldClearCart() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/carts/clear")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(cartClearCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(cartClearCommandHandler);
    }

    @Test
    void cartClear_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/carts/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(cartClearCommandHandler);
    }

    @Test
    void deleteCartProduct_withAuthorizedUser_shouldDeleteCartProduct() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/carts/{productId}", UUID.randomUUID())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        //Assert
        verify(cartDeleteProductCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(cartDeleteProductCommandHandler);
    }

    @Test
    void deleteCartProduct_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/carts/{productId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(cartDeleteProductCommandHandler);
    }
}