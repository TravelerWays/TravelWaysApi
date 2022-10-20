package travel.way.travelwayapi.auth.models.dto.request;

import lombok.Data;

@Data
public class LoginForm {
    private String username;
    private String password;
}
