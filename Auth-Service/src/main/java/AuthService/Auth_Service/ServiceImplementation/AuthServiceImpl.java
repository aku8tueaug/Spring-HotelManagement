package AuthService.Auth_Service.ServiceImplementation;


import AuthService.Auth_Service.DTO.AuthRequestDTO;
import AuthService.Auth_Service.DTO.AuthResponseDTO;
import AuthService.Auth_Service.Entity.RefreshToken;
import AuthService.Auth_Service.Security.Jwt.JwtService;
import AuthService.Auth_Service.Service.AuthServices;
import AuthService.Auth_Service.Service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthServices {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;


    @Override
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {

        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @Override
    public AuthResponseDTO refreshToken(String  refreshToken) {
        //Validation for the refresh Token is expired or correct
        RefreshToken storedToken = refreshTokenService.validateRefreshToken(refreshToken);
        String username = storedToken.getUsername();
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        //In Multi Device scenario, this is critical
        refreshTokenService.revokeToken(refreshToken);
        String newRefreshToken = refreshTokenService.createRefreshToken(username);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return new AuthResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String  refreshToken) {

       refreshTokenService.revokeToken(refreshToken);
    }
}
