package by.cryptic.categoryservice.controller.query;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.read.CategoryView;
import by.cryptic.categoryservice.service.query.handler.CategoryGetAllQueryHandler;
import by.cryptic.categoryservice.service.query.handler.CategoryGetByIdQueryHandler;
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
@WebMvcTest(CategoryQueryController.class)
@ActiveProfiles("mongo")
class CategoryQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryGetAllQueryHandler categoryGetAllQueryHandler;

    @MockitoBean
    private CategoryGetByIdQueryHandler categoryGetByIdQueryHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllCategories_withCategoryThatExists_shouldReturnAllCategories() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(CategoryMapper.toDto(categoryView));
        Mockito.when(categoryGetAllQueryHandler.handle(any())).thenReturn(categoryDTOS);
        //Act
        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDTOS)));
        //Assert
        Mockito.verify(categoryGetAllQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(categoryGetAllQueryHandler);
    }

    @Test
    void getAllCategories_withNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(CategoryMapper.toDto(categoryView));
        Mockito.when(categoryGetAllQueryHandler.handle(any())).thenReturn(categoryDTOS);
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getAllCategories_withAdminRole_shouldReturnAllCategories() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(CategoryMapper.toDto(categoryView));
        Mockito.when(categoryGetAllQueryHandler.handle(any())).thenReturn(categoryDTOS);
        //Act
        mockMvc.perform(get("/api/v1/categories")
                        .with(jwt().jwt(jwt -> {
                            jwt.claim("sub", UUID.randomUUID());
                            jwt.claim("realm_access.roles", "ROLE_ADMIN");
                        })))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDTOS)));
        //Assert
        Mockito.verify(categoryGetAllQueryHandler, times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(categoryGetAllQueryHandler);
    }

    @Test
    @WithMockUser
    void getCategoryById_withCategoryThatExistsAndAuthorizedUser_shouldReturnCategory() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        CategoryDTO categoryDTO = CategoryMapper.toDto(categoryView);
        Mockito.when(categoryGetByIdQueryHandler.handle(categoryId)).thenReturn(categoryDTO);
        //Act
        mockMvc.perform(get("/api/v1/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDTO)));
        //Assert
        Mockito.verify(categoryGetByIdQueryHandler, times(1)).handle(categoryId);
        Mockito.verifyNoMoreInteractions(categoryGetByIdQueryHandler);
    }

    @Test
    void getCategoryById_withCategoryThatExistsAndAdminRole_shouldReturnCategory() throws Exception {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        CategoryDTO categoryDTO = CategoryMapper.toDto(categoryView);
        Mockito.when(categoryGetByIdQueryHandler.handle(categoryId)).thenReturn(categoryDTO);
        //Act
        mockMvc.perform(get("/api/v1/categories/" + categoryId)
                        .with(jwt().jwt(jwt -> {
                            jwt.claim("sub", UUID.randomUUID());
                            jwt.claim("realm_access.roles", "ROLE_ADMIN");
                        })))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryDTO)));
        //Assert
        Mockito.verify(categoryGetByIdQueryHandler, times(1)).handle(categoryId);
        Mockito.verifyNoMoreInteractions(categoryGetByIdQueryHandler);
    }

    @Test
    void getCategoryById_withCategoryThatExistsAndNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/categories/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}