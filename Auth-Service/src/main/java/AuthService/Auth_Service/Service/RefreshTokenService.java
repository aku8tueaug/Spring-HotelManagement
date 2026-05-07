package AuthService.Auth_Service.Service;


import AuthService.Auth_Service.Entity.RefreshToken;

public interface RefreshTokenService {
    public String createRefreshToken(String username);
    public RefreshToken validateRefreshToken(String token);
    public void revokeToken(String token);
}
