package travel.way.travelwayapi.user.models.db;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.way.travelwayapi._core.models.BaseEntity;
import travel.way.travelwayapi._core.models.Roles;
import travel.way.travelwayapi.user.models.dto.request.CreateUserRequest;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser extends BaseEntity {
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    public static AppUser of(@NotNull CreateUserRequest request) {
        return new AppUser(
            request.getName(),
            request.getSurname(),
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            new ArrayList<>()
        );
    }

    public static AppUser of(@NotNull CreateUserRequest request, @NotNull List<Role> roles) {
        return new AppUser(
                request.getName(),
                request.getSurname(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                roles
        );
    }
}
