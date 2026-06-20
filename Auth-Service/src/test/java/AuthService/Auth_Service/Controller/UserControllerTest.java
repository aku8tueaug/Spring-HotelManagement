package AuthService.Auth_Service.Controller;

import AuthService.Auth_Service.DTO.ChangePasswordRequest;
import AuthService.Auth_Service.DTO.CreateUserRequest;
import AuthService.Auth_Service.Security.Jwt.JwtFilter;
import AuthService.Auth_Service.Security.Jwt.JwtService;
import AuthService.Auth_Service.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("old123", "new123");
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.when(authentication.getName()).thenReturn("user1");

        mockMvc.perform(patch("/users/me/password")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password updated successfully"));

        verify(userService).changePassword("user1", "old123", "new123");
    }

    @Test
    void testCreateUser_Success() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newadmin", "adminpass", "ADMIN");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User created"));

        verify(userService).createUser("newadmin", "adminpass", "ADMIN");
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        mockMvc.perform(patch("/users/{username}/role", "user1")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Role updated"));

        verify(userService).updateRole("user1", "ADMIN");
    }

    @Test
    void testDeactivateUser_Success() throws Exception {
        mockMvc.perform(patch("/users/{username}/deactivate", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User deactivated"));

        verify(userService).deactivateUser("user1");
    }
}
