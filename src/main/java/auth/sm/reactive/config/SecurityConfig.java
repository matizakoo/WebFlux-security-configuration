package auth.sm.reactive.config;

import auth.sm.reactive.domain.Users;
import auth.sm.reactive.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private UsersRepository usersRepository;

//    @Bean
//    public MapReactiveUserDetailsService mapReactiveUserDetailsService() {
//        UserDetails userDetails = User.withDefaultPasswordEncoder().username("mati1").password("mati1").roles("USER").build();
//        UserDetails userDetails2 = User.withDefaultPasswordEncoder().username("admin1").password("admin1").roles("ADMIN").build();
//        return new MapReactiveUserDetailsService(userDetails, userDetails2);
//    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(e -> e
                        .pathMatchers("/acc").permitAll()
                        .pathMatchers("/user").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/admin").hasRole("ADMIN")
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService());
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return username -> usersRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
