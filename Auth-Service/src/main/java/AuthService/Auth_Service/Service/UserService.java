package AuthService.Auth_Service.Service;

import AuthService.Auth_Service.DTO.UserResponseDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public void changePassword(String username, String currentPassword, String newPassword);

    public void updateRole(String username, String role);

    public void deactivateUser(String username);

    public void createUser(String username, String password, String role);

    public UserResponseDTO getUserByUserName(String userName);

    public List<UserResponseDTO> getUsers();
}
