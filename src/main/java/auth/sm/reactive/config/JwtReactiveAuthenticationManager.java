package auth.sm.reactive.config;

import auth.sm.reactive.repository.UsersRepository;
import auth.sm.reactive.service.JWTService;
import auth.sm.reactive.service.OwnUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final OwnUserDetailsService ownUserDetailsService;
    private final JWTService jwtService;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String username = jwtService.getUsernameFromToken(token);

        return ownUserDetailsService.findByUsername(username)
                .map(userDetails -> {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    return auth;
                });
    }
}
