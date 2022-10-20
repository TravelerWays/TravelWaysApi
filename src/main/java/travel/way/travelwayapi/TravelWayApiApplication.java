package travel.way.travelwayapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import travel.way.travelwayapi._core.properites.AuthProperties;
import travel.way.travelwayapi.user.models.dto.request.CreateUserRequest;
import travel.way.travelwayapi.user.shared.UserService;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
public class TravelWayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelWayApiApplication.class, args);
    }

    @Bean BCryptPasswordEncoder test(){
        return new BCryptPasswordEncoder();
    }

}
