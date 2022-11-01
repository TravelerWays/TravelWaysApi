package travel.ways.travelwaysapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import travel.ways.travelwaysapi._core.properity.AuthProperty;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi._core.properity.MailProperty;

@SpringBootApplication
@EnableConfigurationProperties({AuthProperty.class, MailProperty.class, CommonProperty.class})
public class TravelWaysApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelWaysApiApplication.class, args);
    }
}
