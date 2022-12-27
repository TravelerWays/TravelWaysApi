package travel.ways.travelwaysapi._core.properity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel.ways.nominatim")
@Data
public class NominatimProperty {
    private String url;
}
