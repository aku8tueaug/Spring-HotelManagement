package AuthService.Auth_Service.Controller;

import AuthService.Auth_Service.DTO.AuthRequestDTO;
import AuthService.Auth_Service.DTO.AuthResponseDTO;
import AuthService.Auth_Service.Security.Jwt.JwtFilter;
import AuthService.Auth_Service.Security.Jwt.JwtService;
import AuthService.Auth_Service.Service.AuthServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthServices authService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void testLogin_Success() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("user1", "pass123");
        AuthResponseDTO response = new AuthResponseDTO("access_tok", "refresh_tok");

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_tok"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_tok"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        String token = "tokenId.secret";
        AuthResponseDTO response = new AuthResponseDTO("new_access", "new_refresh");

        when(authService.refreshToken(token)).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .param("refreshToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh"));

        verify(authService).refreshToken(token);
    }

    @Test
    void testLogout_Success() throws Exception {
        String token = "tokenId.secret";

        mockMvc.perform(post("/auth/logout")
                        .param("refreshToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("You are logged out"));

        verify(authService).logout(token);
    }
}
