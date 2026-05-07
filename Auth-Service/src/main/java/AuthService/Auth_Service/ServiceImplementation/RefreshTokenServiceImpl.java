package AuthService.Auth_Service.ServiceImplementation;


import AuthService.Auth_Service.Entity.RefreshToken;
import AuthService.Auth_Service.Repository.RefreshTokenRepository;
import AuthService.Auth_Service.Service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private static final long REFRESH_TOKEN_DURATION = 7 * 24 * 60 * 60; // seconds

    @Override
    @Transactional
    public String createRefreshToken(String username) {
        //delete old token first
        refreshTokenRepository.deleteByUsername(username);

        // Generate inside service

        String tokenId = UUID.randomUUID().toString().replace("-", "");

        String secret = UUID.randomUUID().toString().replace("-", "");

        String newToken = tokenId + "." + secret;

        String hashedToken = passwordEncoder.encode(secret); //to follow best practice

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .tokenId(tokenId)
                .tokenHash(hashedToken)
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_DURATION))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return newToken;

    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token format");
        }

        String tokenId = parts[0];
        String rawSecret = parts[1];

        RefreshToken dbToken = refreshTokenRepository.findByTokenId(tokenId)

                .orElseThrow(() -> new RuntimeException("Invalid token"));


        if (!passwordEncoder.matches(rawSecret, dbToken.getTokenHash())) {

            throw new RuntimeException("Invalid token");

        }

        if(dbToken.getRevoked())
        {
            log.error("Token revoked, It can not be validated");
            throw new RuntimeException("Token revoked");
        }

        if(dbToken.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            log.error("Token Expired, It can not be validated");
            throw new RuntimeException("Token Expired");
        }

        return dbToken;
    }

    @Override
    public void revokeToken(String token) {

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token format");
        }

        String tokenId = parts[0];
        String rawSecret = parts[1];
       refreshTokenRepository.findByTokenId(tokenId)
                .ifPresentOrElse(t->{
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                },() -> {
                    log.warn("Token is not available, So, Can not be revoked");
                     }
                );

    }
}
