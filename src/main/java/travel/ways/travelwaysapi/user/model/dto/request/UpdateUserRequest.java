package travel.ways.travelwaysapi.user.model.dto.request;

import lombok.*;
import org.springframework.http.HttpStatus;
import travel.ways.travelwaysapi._core.exception.ServerException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    private String email;
    private String name;
    private String surname;

    @SneakyThrows
    public void validAndThrowError(){
        if(email == null || !email.contains("@") || name.length() == 0 || surname.length() == 0){
            throw new ServerException("Invalid model", HttpStatus.BAD_REQUEST);
        }
    }
}
