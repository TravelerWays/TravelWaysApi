package travel.ways.travelwaysapi._core.properity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel.ways")
@Data
public class CommonProperty {
    private String frontAppUrl;
}
