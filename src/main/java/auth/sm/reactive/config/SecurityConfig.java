package auth.sm.reactive.config;

import auth.sm.reactive.controller.AuthController;
import auth.sm.reactive.controller.BasicController;
import auth.sm.reactive.domain.Users;
import auth.sm.reactive.repository.UsersRepository;
import auth.sm.reactive.service.JWTService;
import auth.sm.reactive.service.OwnUserDetailsService;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
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
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.server.ServerWebExchange;
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

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChainBasic(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/basic/**"))
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(BasicController.url + "/test1").permitAll()
                        .pathMatchers(BasicController.url + "/test2").hasRole("USER")
                        .pathMatchers(BasicController.url + "/test3").hasRole("ADMIN")
                        .anyExchange().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .authenticationManager(authenticationManager())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain securityWebFilterChainJwt(ServerHttpSecurity http) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(ownUserDetailsService, jwtService);
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/admin/**"))
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(AuthController.url + "/login", AuthController.url + "/auth", AuthController.url + "/loginpage").permitAll()
                        .pathMatchers(AuthController.url + "/test1").hasRole("USER")
                        .pathMatchers(AuthController.url + "/test2").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
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
