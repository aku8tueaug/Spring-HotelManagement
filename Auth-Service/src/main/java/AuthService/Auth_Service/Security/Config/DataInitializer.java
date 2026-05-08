package AuthService.Auth_Service.Security.Config;


import AuthService.Auth_Service.Entity.AppUser;
import AuthService.Auth_Service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {

            if (userRepository.findByUserName("admin").isEmpty()) {
                userRepository.save(
                        AppUser.builder()
                                .userName("admin")
                                .password(passwordEncoder.encode("admin123"))
                                .role("ADMIN")
                                .active(true)
                                .build()
                );
            }
        };
    }
}
