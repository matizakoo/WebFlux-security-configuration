package auth.sm.reactive.repository;

import auth.sm.reactive.domain.Users;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsersRepository extends ReactiveMongoRepository<Users, String> {
    Mono save (Users users);

    Mono<Users> findByUsername(String username);
}
