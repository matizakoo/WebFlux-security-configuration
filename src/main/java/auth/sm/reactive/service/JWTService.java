package auth.sm.reactive.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class JWTService {
    private final SecretKey key;
    private final JwtParser parser;

    public JWTService() {
        this.key = Keys.hmacShaKeyFor("Keys.hmacShaKeyFor(\"1234567890\".getBytes(StandardCharsets.UTF_8));".getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateTime(UserDetails userDetails, String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();

        boolean isExpired = claims.getExpiration().after(Date.from(Instant.now()));

        return isExpired && userDetails.getUsername() == claims.getSubject();
    }
}
