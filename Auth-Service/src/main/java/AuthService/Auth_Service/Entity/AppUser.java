package AuthService.Auth_Service.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50, name = "username")
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private  String role; // admin / user

    @Column(nullable = false)
    private Boolean active =  true;

}
