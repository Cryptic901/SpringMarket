package by.cryptic.userservice.controller;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.service.handler.UserGetAllQueryHandler;
import by.cryptic.userservice.service.handler.UserGetByIdQueryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
@WebMvcTest(UserQueryController.class)
@ActiveProfiles("mongo")
class UserQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserGetAllQueryHandler userGetAllQueryHandler;

    @MockitoBean
    private UserGetByIdQueryHandler userGetByIdQueryHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllUsers_withAuthorizedUser_shouldReturnAllUsers() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        AppUserView appUserView = AppUserView.builder()
                .userId(userId)
                .username("testUser")
                .phoneNumber("+375453451956")
                .build();
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(UserMapper.toDto(appUserView));
        Mockito.when(userGetAllQueryHandler.handle(any())).thenReturn(userDTOS);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTOS)));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(userDTOS), result);
        Mockito.verify(userGetAllQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(userGetAllQueryHandler);
    }

    @Test
    void getAllUsers_withNotAuthorizedUser_shouldReturnAllUsers() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        AppUserView appUserView = AppUserView.builder()
                .userId(userId)
                .username("testUser")
                .phoneNumber("+375453451956")
                .build();
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(UserMapper.toDto(appUserView));
        Mockito.when(userGetAllQueryHandler.handle(any())).thenReturn(userDTOS);
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_withReturnedUser_shouldReturnUser() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        AppUserView appUserView = AppUserView.builder()
                .userId(userId)
                .username("testUser")
                .phoneNumber("+375453451956")
                .build();
        UserDTO userDTO = UserMapper.toDto(appUserView);
        Mockito.when(userGetByIdQueryHandler.handle(userId)).thenReturn(userDTO);
        //Act
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", userId))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
        //Assert
        Mockito.verify(userGetByIdQueryHandler, times(1)).handle(userId);
        Mockito.verifyNoMoreInteractions(userGetByIdQueryHandler);
    }

    @Test
    void getUserById_withRandomUser_shouldReturnForbidden() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(get("/api/v1/users/" + UUID.randomUUID())
                        .with(jwt().jwt(jwt -> {
                                    jwt.claim("sub", UUID.randomUUID());
                                    jwt.claim("realm_access.roles", "ROLE_USER");
                                }
                        )))
                //Assert
                .andExpect(status().isForbidden());
    }
}