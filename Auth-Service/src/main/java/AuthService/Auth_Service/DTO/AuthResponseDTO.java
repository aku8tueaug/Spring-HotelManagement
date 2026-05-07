package AuthService.Auth_Service.DTO;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {}