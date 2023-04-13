package auth.sm.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class ReactiveApplicationTests {

    @Test
    void contextLoads() {
        Flux.just("bzyk", "randki", "brandki")
                .filter(z -> z.startsWith("b"))
                .subscribe(System.out::println);
    }

}
