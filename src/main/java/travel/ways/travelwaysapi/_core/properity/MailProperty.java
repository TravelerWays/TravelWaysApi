package travel.ways.travelwaysapi._core.properity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel.ways.mail")
@Data
public class MailProperty {
    private String host;
    private String username;
    private String password;
    private int port;
    private String protocol;
    private String from;
    private String mailTemplatePath;
    private boolean send;
}
