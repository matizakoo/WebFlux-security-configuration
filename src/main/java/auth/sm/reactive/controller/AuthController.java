package auth.sm.reactive.controller;

import auth.sm.reactive.model.reqBody.ReqLogin;
import auth.sm.reactive.model.respModel.ReqRespModel;
import auth.sm.reactive.service.JWTService;
import auth.sm.reactive.service.OwnUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthController.url)
@AllArgsConstructor
public class AuthController {
    public static final String url = "/admin";
    private final ReactiveUserDetailsService users;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final OwnUserDetailsService ownUserDetailsService;

    @GetMapping("/test")
    public Mono<ResponseEntity<ReqRespModel<String>>> auth() {
        return Mono.just(
                ResponseEntity.ok(
                        new ReqRespModel<>("Hello in JWT :) ", "")
                ));
    }

    //tested in postman
    @PostMapping("/login")
    public Mono<ResponseEntity<ReqRespModel<String>>> login(@RequestBody ReqLogin user) throws InterruptedException {
        System.out.println(user.getUsername() + " " + user.getPassword());
        Mono<UserDetails> foundUser = ownUserDetailsService.findByUsername(user.getUsername());
        return foundUser.flatMap(u -> {
            if (u != null) {
                if (passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                    return Mono.just(
                            ResponseEntity.ok(
                                    new ReqRespModel<>(jwtService.generateToken(user.getUsername()), "Na pewno nie zadziala")
                            )
                    );
                }
                return Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>(null, "Invalid credentials"))
                );
            }

            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>("", "User not found")));
        });
    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> authnew(@RequestBody ReqLogin user) {
        System.out.println(user.getUsername() + " " + user.getPassword());
        Mono<UserDetails> foundUser = ownUserDetailsService.findByUsername(user.getUsername());
        return foundUser.flatMap(u -> {
            if (u != null) {
                if (passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                    return Mono.just(
                            ResponseEntity.ok(
                                    new ReqRespModel<>(jwtService.generateToken(user.getUsername()), "Na pewno nie zadziala")
                            )
                    );
                }
                return Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>(null, "Invalid credentials"))
                );
            }

            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>("", "User not found")));
        });
//        return Mono.just(ResponseEntity.ok("ok"));
    }

    @GetMapping("/z")
    public Mono<ResponseEntity<?>> da() {
        return Mono.just(ResponseEntity.ok("da"));
    }

    @GetMapping("/loginpage")
    public Mono<ResponseEntity<?>> loginPage(){
        return Mono.just(ResponseEntity.ok("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <title>Login</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "hi helo\n" +
                "<form action=\"/admin/auth\" method=\"post\">\n" +
                "    <label for=\"username\">Username:</label>\n" +
                "    <input type=\"text\" id=\"username\" name=\"username\" required=\"required\" autofocus=\"autofocus\" />\n" +
                "    <br/>\n" +
                "    <label for=\"password\">Password:</label>\n" +
                "    <input type=\"password\" id=\"password\" name=\"password\" required=\"required\" />\n" +
                "    <br/>\n" +
                "    <button type=\"submit\">srititti</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>"));
    }
}
