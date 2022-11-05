package travel.ways.travelwaysapi._bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.service.internal.AccountService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

@Configuration
@RequiredArgsConstructor
public class Bootstrap {
    private final UserService userService;
    private final AccountService accountService;
    private final RoleRepository roleRepository;

    @Bean
    @Transactional
    public CommandLineRunner setupRoles() {
        return args -> {
            for (var roleName : Roles.GetAllRoles()) {
                if (!roleRepository.existsByName(roleName)) {
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
            var createUser = new CreateUserRequest(
                    "John",
                    "Doe",
                    "JD",
                    "elo",
                    "kazmierczakkarol1999@gmail.com"
            );

            accountService.registerUser(createUser);
        };
    }
}
