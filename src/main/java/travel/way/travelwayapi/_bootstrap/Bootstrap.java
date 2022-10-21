package travel.way.travelwayapi._bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import travel.way.travelwayapi._core.models.Roles;
import travel.way.travelwayapi.user.models.db.Role;
import travel.way.travelwayapi.user.models.dto.request.CreateUserRequest;
import travel.way.travelwayapi.user.repository.RoleRepository;
import travel.way.travelwayapi.user.shared.UserService;

@Configuration
@RequiredArgsConstructor
public class Bootstrap {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @Bean
    @Transactional
    public CommandLineRunner setupRoles() {
        return args -> {
            for (var roleName : Roles.GetAllRoles()) {
                if (roleRepository.existsByName(roleName)) {
                    roleRepository.save(new Role(roleName));
                }
            }
        };
    }

    @Bean
    @Profile("dev")
    @DependsOn({"setupRoles"})
    public CommandLineRunner run() {
        return args -> {
            var role = roleRepository.findByName(Roles.ROLE_USER);

            var createUser = new CreateUserRequest(
                    "Jhon",
                    "Doe",
                    "JD",
                    "elo",
                    "email@gmail.com"
            );

            userService.createUser(createUser);
        };
    }
}
