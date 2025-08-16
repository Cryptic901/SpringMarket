package by.cryptic.userservice.service;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.userservice.service.handler.UserGetByIdQueryHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserGetByIdQueryHandlerTest {

    @Mock
    private UserViewRepository userViewRepository;

    @InjectMocks
    private UserGetByIdQueryHandler userGetByIdQueryHandler;

    @Test
    void getUserById_withValidUUID_shouldReturnUser() {
        //Arrange
        UUID userId = UUID.randomUUID();
        AppUserView appUserView = AppUserView.builder()
                .userId(userId)
                .username("testUser")
                .phoneNumber("+375453451956")
                .build();
        UserDTO userDTO = new UserDTO(appUserView.getUsername(), appUserView.getPhoneNumber());
        Mockito.when(userViewRepository.findById(userId)).thenReturn(Optional.of(appUserView));
        //Act
        UserDTO result = userGetByIdQueryHandler.handle(userId);
        //Assert
        assertEquals(userDTO, result);
        Mockito.verify(userViewRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userViewRepository);
    }

    @Test
    void getUserById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID userId = UUID.randomUUID();
        Mockito.when(userViewRepository.findById(userId)).thenReturn(Optional.empty());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> userGetByIdQueryHandler.handle(userId));
        Mockito.verify(userViewRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userViewRepository);
    }
}