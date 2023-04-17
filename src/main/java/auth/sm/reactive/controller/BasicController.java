package auth.sm.reactive.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping(BasicController.url)
@AllArgsConstructor
public class BasicController {
    public static final String url = "/basic";

    @GetMapping(value = "/test1", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<String> getTest1() {
        return Flux.just("Adam", "Alice", "Bartosz", "Cecylia", "Daniel", "Eliza",
                "Felicja", "Gabriel", "Hanna", "Ignacy", "Julia", "Konrad", "Laura", "Marek", "Natalia", "Oskar",
                "Paulina", "Radosław", "Sylwia", "Tomasz", "Urszula", "Wojciech", "Zuzanna");
    }

    @GetMapping(value = "/test2", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<String> getTest2() {
        return Flux.just("Hello", "from", "basicauth");
    }

    @GetMapping(value = "/test3", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<String> getTest3() {
        return Flux.just("Hello", "from", "basicauth ", "admin :) ");
    }
}
