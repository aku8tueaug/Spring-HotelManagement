package AuthService.Auth_Service.Service;

public interface UserService {
    public void changePassword(String username,String currentPassword, String newPassword);
    public void updateRole(String username, String role);
    public void deactivateUser(String username);
    public void createUser(String username, String password, String role);
}
