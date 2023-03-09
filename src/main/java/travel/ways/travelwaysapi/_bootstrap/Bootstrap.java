package travel.ways.travelwaysapi._bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.map.service.shared.LocationService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

@Configuration
@RequiredArgsConstructor
public class Bootstrap {
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final LocationService locationService;
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
    @Transactional
    @Profile({"dev", "test"})
    public CommandLineRunner setupLocation() {
        return args -> {
            CreateLocationRequest createLocationRequest = new CreateLocationRequest(
                    "name",
                    "54.434",
                    "43.343",
                    "display_name",
                    "osm_id"
            );
            locationService.create(createLocationRequest);
        };
    }

    @Bean
    @Profile({"dev", "test"})
    @DependsOn({"setupRoles"})
    public CommandLineRunner run() {
        return args -> {
            var createUser = new CreateUserRequest(
                    "John",
                    "Doe",
                    "JD",
                    "elo",
                    "test@example.com"
            );
            if(userService.getByUsername(createUser.getUsername()) == null){
                AppUser user = accountService.createUser(createUser);
                accountService.activateUser(user.getHash());
            }

            var createUser2 = new CreateUserRequest(
                    "John_2",
                    "Doe_2",
                    "JD_2",
                    "elo",
                    "test2@example.com"
            );
            if(userService.getByUsername(createUser2.getUsername()) == null){
                AppUser user = accountService.createUser(createUser2);
                accountService.activateUser(user.getHash());
            }

        };
    }
}
