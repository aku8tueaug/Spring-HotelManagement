package AuthService.Auth_Service.Controller;


import AuthService.Auth_Service.DTO.ChangePasswordRequest;
import AuthService.Auth_Service.DTO.CreateUserRequest;
import AuthService.Auth_Service.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Logged-in user changes own password
    //Allow this method ONLY if the user is authenticated
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String username = authentication.getName(); // from JWT

        userService.changePassword(
                username,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.ok("Password updated successfully");
    }

    // Admin creates new user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {

        userService.createUser(
                request.username(),
                request.password(),
                request.role()
        );

        return ResponseEntity.ok("User created");
    }

    // Admin updates role
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable String username,
            @RequestParam String role) {

        userService.updateRole(username, role);
        return ResponseEntity.ok("Role updated");
    }

    // Admin deactivates user
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{username}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable String username) {

        userService.deactivateUser(username);
        return ResponseEntity.ok("User deactivated");
    }
}
