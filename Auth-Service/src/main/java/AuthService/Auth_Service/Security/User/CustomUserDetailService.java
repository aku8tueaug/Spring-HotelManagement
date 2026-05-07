package AuthService.Auth_Service.Security.User;


import AuthService.Auth_Service.Entity.AppUser;
import AuthService.Auth_Service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not Found"));

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole())
                .disabled(!user.getActive())
                .build();

    }
}
