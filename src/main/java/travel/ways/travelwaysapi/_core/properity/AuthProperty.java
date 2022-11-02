package travel.ways.travelwaysapi._core.properity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "travel.ways.auth")
@Data
@Primary
public class AuthProperty {
    private String secret;
}
