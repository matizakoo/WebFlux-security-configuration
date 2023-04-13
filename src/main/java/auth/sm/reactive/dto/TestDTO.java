package auth.sm.reactive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestDTO {
    private String id;
    private String name;
}
