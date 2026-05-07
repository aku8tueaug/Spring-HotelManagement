package AuthService.Auth_Service.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenId;

    @Column(nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean revoked = false;
}