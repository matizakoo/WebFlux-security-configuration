package auth.sm.reactive.controller;

import auth.sm.reactive.domain.Test;
import auth.sm.reactive.domain.Users;
import auth.sm.reactive.repository.TestRepository;
import auth.sm.reactive.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.time.Duration;
import java.util.Random;

@RestController
@AllArgsConstructor
public class TestController {
    private TestRepository testRepository;
    private UsersRepository usersRepository;

    private static final Flux<String> names = Flux.just(
            "Emily", "William", "Sophia", "Benjamin", "Madison", "Jackson", "Olivia"
    );

    private static final Random random = new Random();

    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<String> get() {
        return Flux.just("Adam", "Alice", "Bartosz", "Cecylia", "Daniel", "Eliza",
                "Felicja", "Gabriel", "Hanna", "Ignacy", "Julia", "Konrad", "Laura", "Marek", "Natalia", "Oskar",
                "Paulina", "Radosław", "Sylwia", "Tomasz", "Urszula", "Wojciech", "Zuzanna");
    }

    @GetMapping(value = "/acc", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Test> getAll() {
        return testRepository.findAll()
                .delayElements(Duration.ofSeconds(1))
                .map(test -> Test.builder().name(test.getName().toUpperCase()).build())
                .zipWith(Flux.range(1, Integer.MAX_VALUE))
                .map(tuple -> {
                    Test test = tuple.getT1();
                    String index = tuple.getT2().toString();
                    test.setId(index);
                    return test;
                });
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Users> getUsers() {
        return usersRepository.findAll();
//                .delayElements(Duration.ofNanos(100))
//                .map(users -> Users.builder()
//                        .username(users.getUsername())
//                        .password("hidden")
//                        .role(null)
//                        .build())
    }

    @GetMapping(value = "/add")
    public Mono<Test> addTest() {
        return names
                .collectList()
                .map(list -> list.get(new Random().nextInt(list.size())))
                .map(randomName -> Test.builder().name(randomName).build())
                .flatMap(testRepository::save);
    }
}
