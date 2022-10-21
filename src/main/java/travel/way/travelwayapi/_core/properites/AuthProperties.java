package travel.way.travelwayapi._core.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel.way.auth")
@Data
public class AuthProperties {
    private String secret;
}