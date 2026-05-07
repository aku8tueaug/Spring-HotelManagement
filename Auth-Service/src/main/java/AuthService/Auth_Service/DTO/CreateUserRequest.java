package AuthService.Auth_Service.DTO;

public record CreateUserRequest(
        String username,
        String password,
        String role
) {}
