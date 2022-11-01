package travel.ways.travelwaysapi._core.properity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel.ways")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommonProperty {
    private String frontAppUrl;
}
