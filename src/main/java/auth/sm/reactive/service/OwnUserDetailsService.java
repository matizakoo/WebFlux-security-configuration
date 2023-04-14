package auth.sm.reactive.service;


import auth.sm.reactive.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class OwnUserDetailsService {
    private final UsersRepository usersRepository;
    public Mono<UserDetails> findByUsername(String username) {
        return usersRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build());
    }
}
