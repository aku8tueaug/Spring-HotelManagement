package AuthService.Auth_Service.DTO;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
