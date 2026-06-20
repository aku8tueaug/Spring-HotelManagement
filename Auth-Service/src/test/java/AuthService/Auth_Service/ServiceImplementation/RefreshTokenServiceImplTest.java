package AuthService.Auth_Service.ServiceImplementation;

import AuthService.Auth_Service.Entity.RefreshToken;
import AuthService.Auth_Service.Repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void testCreateRefreshToken() {
        String username = "user1";
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("hashedSecret");

        String returnedToken = refreshTokenService.createRefreshToken(username);

        assertNotNull(returnedToken);
        String[] parts = returnedToken.split("\\.");
        assertEquals(2, parts.length);

        verify(refreshTokenRepository).deleteByUsername(username);
        verify(refreshTokenRepository).save(argThat(token ->
                token.getUsername().equals(username) &&
                token.getTokenId().equals(parts[0]) &&
                token.getTokenHash().equals("hashedSecret") &&
                !token.getRevoked() &&
                token.getExpiryDate().isAfter(LocalDateTime.now())
        ));
    }

    @Test
    void testValidateRefreshToken_Success() {
        String token = "myTokenId.myTokenSecret";
        RefreshToken dbToken = RefreshToken.builder()
                .tokenId("myTokenId")
                .tokenHash("hashedSecret")
                .username("user1")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenId("myTokenId")).thenReturn(Optional.of(dbToken));
        when(passwordEncoder.matches("myTokenSecret", "hashedSecret")).thenReturn(true);

        RefreshToken result = refreshTokenService.validateRefreshToken(token);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void testValidateRefreshToken_InvalidFormat_ThrowsException() {
        String tokenWithoutDot = "invalidTokenFormat";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validateRefreshToken(tokenWithoutDot);
        });

        assertEquals("Invalid token format", exception.getMessage());
        verifyNoInteractions(refreshTokenRepository, passwordEncoder);
    }

    @Test
    void testValidateRefreshToken_TokenNotFound_ThrowsException() {
        String token = "unknownId.secret";
        when(refreshTokenRepository.findByTokenId("unknownId")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validateRefreshToken(token);
        });

        assertEquals("Invalid token", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void testValidateRefreshToken_SecretMismatch_ThrowsException() {
        String token = "tokenId.wrongSecret";
        RefreshToken dbToken = RefreshToken.builder()
                .tokenId("tokenId")
                .tokenHash("hashedSecret")
                .build();

        when(refreshTokenRepository.findByTokenId("tokenId")).thenReturn(Optional.of(dbToken));
        when(passwordEncoder.matches("wrongSecret", "hashedSecret")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validateRefreshToken(token);
        });

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testValidateRefreshToken_TokenRevoked_ThrowsException() {
        String token = "tokenId.secret";
        RefreshToken dbToken = RefreshToken.builder()
                .tokenId("tokenId")
                .tokenHash("hashedSecret")
                .revoked(true)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        when(refreshTokenRepository.findByTokenId("tokenId")).thenReturn(Optional.of(dbToken));
        when(passwordEncoder.matches("secret", "hashedSecret")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validateRefreshToken(token);
        });

        assertEquals("Token revoked", exception.getMessage());
    }

    @Test
    void testValidateRefreshToken_TokenExpired_ThrowsException() {
        String token = "tokenId.secret";
        RefreshToken dbToken = RefreshToken.builder()
                .tokenId("tokenId")
                .tokenHash("hashedSecret")
                .revoked(false)
                .expiryDate(LocalDateTime.now().minusMinutes(5)) // Expired
                .build();

        when(refreshTokenRepository.findByTokenId("tokenId")).thenReturn(Optional.of(dbToken));
        when(passwordEncoder.matches("secret", "hashedSecret")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validateRefreshToken(token);
        });

        assertEquals("Token Expired", exception.getMessage());
    }

    @Test
    void testRevokeToken_Success() {
        String token = "tokenId.secret";
        RefreshToken dbToken = RefreshToken.builder()
                .tokenId("tokenId")
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenId("tokenId")).thenReturn(Optional.of(dbToken));

        refreshTokenService.revokeToken(token);

        assertTrue(dbToken.getRevoked());
        verify(refreshTokenRepository).save(dbToken);
    }

    @Test
    void testRevokeToken_InvalidFormat_ThrowsException() {
        String token = "invalidFormat";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.revokeToken(token);
        });

        assertEquals("Invalid token format", exception.getMessage());
        verifyNoInteractions(refreshTokenRepository);
    }
}
