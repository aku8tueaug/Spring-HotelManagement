package AuthService.Auth_Service.ServiceImplementation;

import AuthService.Auth_Service.Entity.AppUser;
import AuthService.Auth_Service.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testChangePassword_Success() {
        AppUser user = AppUser.builder()
                .userName("user1")
                .password("hashedCurrentPass")
                .role("USER")
                .active(true)
                .build();

        when(userRepository.findByUserName("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPass", "hashedCurrentPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");

        userService.changePassword("user1", "currentPass", "newPass");

        assertEquals("hashedNewPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("unknown", "current", "new");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangePassword_IncorrectCurrentPassword_ThrowsException() {
        AppUser user = AppUser.builder()
                .userName("user1")
                .password("hashedCurrentPass")
                .role("USER")
                .active(true)
                .build();

        when(userRepository.findByUserName("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedCurrentPass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("user1", "wrongPass", "newPass");
        });

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateRole_Success() {
        AppUser user = AppUser.builder()
                .userName("user1")
                .password("pass")
                .role("USER")
                .active(true)
                .build();

        when(userRepository.findByUserName("user1")).thenReturn(Optional.of(user));

        userService.updateRole("user1", "ADMIN");

        assertEquals("ADMIN", user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateRole_UserNotFound_ThrowsException() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateRole("unknown", "ADMIN");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByUserName("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");

        userService.createUser("newuser", "plainPass", "USER");

        verify(userRepository).save(argThat(user ->
                user.getUserName().equals("newuser") &&
                user.getPassword().equals("encodedPass") &&
                user.getRole().equals("USER") &&
                user.getActive()
        ));
    }

    @Test
    void testCreateUser_UsernameExists_ThrowsException() {
        AppUser existingUser = AppUser.builder().userName("existing").build();
        when(userRepository.findByUserName("existing")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser("existing", "pass", "USER");
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeactivateUser_Success() {
        AppUser user = AppUser.builder()
                .userName("user1")
                .active(true)
                .build();

        when(userRepository.findByUserName("user1")).thenReturn(Optional.of(user));

        userService.deactivateUser("user1");

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void testDeactivateUser_UserNotFound_ThrowsException() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deactivateUser("unknown");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
