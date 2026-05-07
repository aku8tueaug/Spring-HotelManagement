package AuthService.Auth_Service.Service;


import AuthService.Auth_Service.DTO.AuthRequestDTO;
import AuthService.Auth_Service.DTO.AuthResponseDTO;

public interface AuthServices {
    AuthResponseDTO login(AuthRequestDTO request);
    AuthResponseDTO refreshToken(String  refreshToken);
    void logout(String  refreshToken);
}
