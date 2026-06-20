package AuthService.Auth_Service.ServiceImplementation;

import AuthService.Auth_Service.DTO.AuthRequestDTO;
import AuthService.Auth_Service.DTO.AuthResponseDTO;
import AuthService.Auth_Service.Entity.RefreshToken;
import AuthService.Auth_Service.Security.Jwt.JwtService;
import AuthService.Auth_Service.Service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin_Success() {
        AuthRequestDTO request = new AuthRequestDTO("user1", "pass123");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user1");
        when(jwtService.generateAccessToken(userDetails)).thenReturn("fakeAccessToken");
        when(refreshTokenService.createRefreshToken("user1")).thenReturn("fakeRefreshToken");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("fakeAccessToken", response.accessToken());
        assertEquals("fakeRefreshToken", response.refreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(userDetails);
        verify(refreshTokenService).createRefreshToken("user1");
    }

    @Test
    void testLogin_BadCredentials_ThrowsException() {
        AuthRequestDTO request = new AuthRequestDTO("user1", "wrongpass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Bad credentials", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService, refreshTokenService);
    }

    @Test
    void testRefreshToken_Success() {
        String oldRefreshToken = "oldId.oldSecret";
        RefreshToken storedToken = RefreshToken.builder()
                .username("user1")
                .tokenId("oldId")
                .tokenHash("oldSecretHashed")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        UserDetails userDetails = mock(UserDetails.class);

        when(refreshTokenService.validateRefreshToken(oldRefreshToken)).thenReturn(storedToken);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(refreshTokenService.createRefreshToken("user1")).thenReturn("newId.newSecret");
        when(jwtService.generateAccessToken(userDetails)).thenReturn("newAccessToken");

        AuthResponseDTO response = authService.refreshToken(oldRefreshToken);

        assertNotNull(response);
        assertEquals("newAccessToken", response.accessToken());
        assertEquals("newId.newSecret", response.refreshToken());
        verify(refreshTokenService).validateRefreshToken(oldRefreshToken);
        verify(refreshTokenService).revokeToken(oldRefreshToken);
        verify(refreshTokenService).createRefreshToken("user1");
        verify(jwtService).generateAccessToken(userDetails);
    }

    @Test
    void testLogout_Success() {
        String token = "tokenId.tokenSecret";
        authService.logout(token);
        verify(refreshTokenService).revokeToken(token);
    }
}
