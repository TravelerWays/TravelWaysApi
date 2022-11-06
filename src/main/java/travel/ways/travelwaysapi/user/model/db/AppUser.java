package travel.ways.travelwaysapi.user.model.db;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

import javax.persistence.*;
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
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean active;
    @Column(unique = true)
    private String hash;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(targetEntity = PasswordRecovery.class, mappedBy = "user")
    private Collection<PasswordRecovery> passwordRecoveries = new ArrayList<>();

    public AppUser(String name, String surname, String username, String email, String password, Collection<Role> roles) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

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
