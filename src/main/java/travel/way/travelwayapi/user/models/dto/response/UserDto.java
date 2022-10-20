package travel.way.travelwayapi.user.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.way.travelwayapi.user.models.db.AppUser;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private Collection<RoleDto> roles = new ArrayList<>();

    public static UserDto of(AppUser user){
        var roles = user.getRoles().stream().map(role -> {return  new RoleDto(role.getName());}).toList();
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}
