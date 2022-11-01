package travel.ways.travelwaysapi._core.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderBean {

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoderBean() {
        return new BCryptPasswordEncoder();
    }
}
