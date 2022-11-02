package travel.ways.travelwaysapi.auth.model.dto.request;

import lombok.Data;

@Data
public class LoginForm {
    private String username;
    private String password;
}
