package AuthService.Auth_Service.Controller;


import AuthService.Auth_Service.DTO.AuthRequestDTO;
import AuthService.Auth_Service.DTO.AuthResponseDTO;
import AuthService.Auth_Service.Service.AuthServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServices authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO request) {

            AuthResponseDTO authResponseDTO = authService.login(request);

        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestParam String refreshToken) {

            AuthResponseDTO authResponseDTO = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestParam String refreshToken) {

        authService.logout(refreshToken);

        return ResponseEntity.ok("You are logged out");
    }
}
