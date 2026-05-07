package AuthService.Auth_Service.Security.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY = Base64.getEncoder()
                                        .encodeToString("mysecretkeymysecretkeymysecretkey123".getBytes());

    private static final long ACCESS_TOKEN_EXPIRY = 1000*60; //1000 * 60 * 15; // 15 min

    public String generateAccessToken(String username) {
        return buildToken(username, ACCESS_TOKEN_EXPIRY);
    }

    private String buildToken(String username, long expiry) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    //Extract username
    public String extractUsername(String token) {
        return extractAllClaims(token)
                .getSubject();
    }

    //Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    //Check expiry
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    //Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
    }

    // 🔑 Signing key
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
