package auth.sm.reactive.config;

import auth.sm.reactive.controller.AuthController;
import auth.sm.reactive.domain.Users;
import auth.sm.reactive.repository.UsersRepository;
import auth.sm.reactive.service.JWTService;
import auth.sm.reactive.service.OwnUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private OwnUserDetailsService ownUserDetailsService;

    //in memory auth
//    @Bean
//    public MapReactiveUserDetailsService mapReactiveUserDetailsService() {
//        UserDetails userDetails = User.withDefaultPasswordEncoder().username("mati1").password("mati1").roles("USER").build();
//        UserDetails userDetails2 = User.withDefaultPasswordEncoder().username("admin1").password("admin1").roles("ADMIN").build();
//        return new MapReactiveUserDetailsService(userDetails, userDetails2);
//    }

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(e -> e
//                        .pathMatchers("/acc").permitAll()
//                        .pathMatchers("/user").hasAnyRole("USER", "ADMIN")
//                        .pathMatchers("/admin").hasRole("ADMIN")
//                )
//                .httpBasic(Customizer.withDefaults())
//                .build();
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChainJwt(ServerHttpSecurity http, AuthConverter jwtAuthConverter, AuthManager jwtAuthManager) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(ownUserDetailsService, jwtService);
        return http
                .authorizeExchange(auth -> {
                    auth.pathMatchers(AuthController.url + "/login").permitAll();
                    auth.pathMatchers(AuthController.url + "/auth").permitAll();
                    auth.pathMatchers(AuthController.url + "/loginpage").permitAll();
                    auth.pathMatchers("/acc").hasRole("USER");  //works
                    auth.pathMatchers("/admin/test").hasRole("ADMIN");  //works
                    auth.anyExchange().authenticated();
                })
                .addFilterAt(authenticationWebFilter(jwtAuthenticationConverter, jwtReactiveAuthenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .build();
    }

    private AuthenticationWebFilter authenticationWebFilter(JwtAuthenticationConverter jwtAuthenticationConverter, JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtReactiveAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        return authenticationWebFilter;
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
