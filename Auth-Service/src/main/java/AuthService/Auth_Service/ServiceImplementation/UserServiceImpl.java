package AuthService.Auth_Service.ServiceImplementation;

import AuthService.Auth_Service.DTO.UserResponseDTO;
import AuthService.Auth_Service.Entity.AppUser;
import AuthService.Auth_Service.Repository.UserRepository;
import AuthService.Auth_Service.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // User changes own password
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void changePassword(String username, String currentPassword, String newPassword) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Admin updates role
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void updateRole(String username, String role) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    // Admin creates user
    @Transactional
    public void createUser(String username, String password, String role) {

        if (userRepository.findByUserName(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        userRepository.save(AppUser.builder()
                .userName(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build());
    }

    @Override
    public UserResponseDTO getUserByUserName(String userName) {
        AppUser user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("No user with provided userName"));
        return toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getUsers() {
        List<AppUser> users = userRepository.findAll();
        return toResponseDTOList(users);

    }

    // Admin deactivates user
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void deactivateUser(String username) {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    // Helper Method
    private UserResponseDTO toResponseDTO(AppUser user) {
        return UserResponseDTO.builder()
                .userName(user.getUserName())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }

    private List<UserResponseDTO> toResponseDTOList(List<AppUser> users ) {
        return users.stream().map(this::toResponseDTO).toList();
    }
}
