package auth.sm.reactive.repository;

import auth.sm.reactive.domain.Test;
import auth.sm.reactive.dto.TestDTO;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends ReactiveMongoRepository<Test, String> {
    Publisher<?> save(TestDTO testDTO);
}
