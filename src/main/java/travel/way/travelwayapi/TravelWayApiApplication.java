package travel.way.travelwayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import travel.way.travelwayapi._core.properites.AuthProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
public class TravelWayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelWayApiApplication.class, args);
    }
}
