package auth.sm.reactive.service;

import auth.sm.reactive.domain.Test;
import auth.sm.reactive.domain.Users;
import auth.sm.reactive.dto.TestDTO;
import auth.sm.reactive.repository.TestRepository;
import auth.sm.reactive.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class InitService {
    private final TestRepository testRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void save() {
        testRepository.deleteAll()
                .thenMany(Flux.just("Adam", "Alice", "Bartosz", "Cecylia", "Daniel", "Eliza",
                        "Felicja", "Gabriel", "Hanna", "Ignacy", "Julia", "Konrad", "Laura", "Marek", "Pys", "Oskar",
                        "Paulina", "Radosław", "Sylwia", "Tomasz", "Urszula", "Wojciech", "Irmina"))
                .map(name -> Test.builder().name(name).build())
                .flatMap(testRepository::save)
                .thenMany(testRepository.findAll())
                .map(test -> "ID: " + test.getId() + "name: " + test.getName())
                .subscribe(System.out::println);

        usersRepository
                .deleteAll()
                .thenMany(Flux.just(Users.builder()
                                .username("mati")
                                .password(passwordEncoder.encode("mati"))
                                .role("USER")
                                .build(),
                        Users.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .role("ADMIN")
                                .build()
                        ))
                .flatMap(usersRepository::save)
                .subscribe();
    }
}
