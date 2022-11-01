package travel.ways.travelwaysapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import travel.ways.travelwaysapi._core.properite.AuthProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
public class TravelWaysApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelWaysApiApplication.class, args);
    }
}
