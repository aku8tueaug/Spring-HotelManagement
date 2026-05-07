package AuthService.Auth_Service.Repository;


import AuthService.Auth_Service.Entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser,Long> {
    Optional<AppUser> findByUsername(String username);
}
