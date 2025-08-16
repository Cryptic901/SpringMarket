package by.cryptic.userservice.service;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.userservice.service.handler.UserGetAllQueryHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserGetAllQueryHandlerTest {

    @Mock
    private UserViewRepository userViewRepository;

    @InjectMocks
    private UserGetAllQueryHandler userGetAllQueryHandler;

    @Test
    void getAllUsers_withUsers_shouldReturnAllUsers() {
        //Arrange
        UUID userId = UUID.randomUUID();
        AppUserView appUserView = AppUserView.builder()
                .userId(userId)
                .username("testUser")
                .phoneNumber("+375453451956")
                .build();
        UserDTO userDTO = new UserDTO(appUserView.getUsername(), appUserView.getPhoneNumber());
        Mockito.when(userViewRepository.findAll()).thenReturn(Collections.singletonList(appUserView));
        //Act
        List<UserDTO> result = userGetAllQueryHandler.handle(new UserGetAllQuery());
        //Assert
        assertEquals(Collections.singletonList(userDTO), result);
        Mockito.verify(userViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userViewRepository);
    }

    @Test
    void getAllUsers_withNoUsers_shouldReturnEntityNotFoundException() {
        //Arrange
        Mockito.when(userViewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> userGetAllQueryHandler.handle(new UserGetAllQuery()));
        Mockito.verify(userViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userViewRepository);
    }
}