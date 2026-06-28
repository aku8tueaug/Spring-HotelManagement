package AuthService.Auth_Service.DTO;

import lombok.Builder;

@Builder
public record UserResponseDTO(
        Long userId,
        String userName,
        String role,
        Boolean active
) {
}
