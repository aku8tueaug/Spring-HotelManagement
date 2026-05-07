package AuthService.Auth_Service.Repository;


import AuthService.Auth_Service.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByTokenId(String token);

    void deleteByUsername(String username);
}
