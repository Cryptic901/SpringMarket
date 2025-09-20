package by.cryptic.analyticservice.controller;

import by.cryptic.analyticservice.listener.AnalyticListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(AdminAnalyticController.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
public class AdminAnalyticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAnalyticController adminAnalyticController;

    @MockitoBean
    private AnalyticListener analyticListener;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDashboard_withAdminRoots_shouldReturnDashboardStats() throws Exception {
        //Arrange
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/admin/dashboard")
                .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
    }
}
