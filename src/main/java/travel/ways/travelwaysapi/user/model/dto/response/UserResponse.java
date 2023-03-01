package travel.ways.travelwaysapi.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String hash;
    private String name;
    private String surname;
    private String username;
    private String email;
    private Collection<RoleResponse> roles = new ArrayList<>();
    private ImageSummaryDto image;

    public static UserResponse of(AppUser user, ImageSummaryDto imageSummary) {
        var roles = user.getRoles().stream().map(role -> new RoleResponse(role.getName())).toList();
        return new UserResponse(
                user.getHash(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                roles,
                imageSummary
        );
    }
}
