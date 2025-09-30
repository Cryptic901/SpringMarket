package by.cryptic.categoryservice.controller.command;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.service.command.CategoryCreateCommand;
import by.cryptic.categoryservice.service.command.handler.CategoryCreateCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryDeleteCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryUpdateCommandHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(CategoryCommandController.class)
@ActiveProfiles(value = {"jpa", "security", "test"})
class CategoryCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CategoryCreateCommandHandler categoryCreateCommandHandler;

    @MockitoBean
    private CategoryUpdateCommandHandler categoryUpdateCommandHandler;

    @MockitoBean
    private CategoryDeleteCommandHandler categoryDeleteCommandHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_withAuthorizedUser_shouldReturnCreated() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        CategoryCreateCommand categoryCreateCommand = new CategoryCreateCommand(
                category.getName(),
                category.getDescription()
        );
        String json = objectMapper.writeValueAsString(categoryCreateCommand);
        //Act
        mockMvc.perform(post("/api/v1/categories")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        //Assert
        verify(categoryCreateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(categoryCreateCommandHandler);
    }

    @Test
    void createCategory_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        CategoryCreateCommand categoryCreateCommand = new CategoryCreateCommand(
                category.getName(),
                category.getDescription()
        );
        String json = objectMapper.writeValueAsString(categoryCreateCommand);
        //Act
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(categoryCreateCommandHandler);
    }

    @Test
    void updateCategory_withAuthorizedUser_shouldReturnNoContent() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO(
                "newTitle", null);
        String json = objectMapper.writeValueAsString(categoryDTO);
        //Act
        mockMvc.perform(patch("/api/v1/categories/" + categoryId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
        //Assert
        verify(categoryUpdateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(categoryUpdateCommandHandler);
    }

    @Test
    void updateCategory_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryUpdateDTO = new CategoryDTO(
                "newTitle", null);
        String json = objectMapper.writeValueAsString(categoryUpdateDTO);
        //Act
        mockMvc.perform(patch("/api/v1/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(categoryUpdateCommandHandler);
    }

    @Test
    void deleteCategory_withAuthorizedUser_shouldReturnNoContent() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/categories/" + UUID.randomUUID())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        //Assert
        verify(categoryDeleteCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(categoryDeleteCommandHandler);
    }

    @Test
    void deleteCategory_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(delete("/api/v1/categories/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(categoryDeleteCommandHandler);
    }
}