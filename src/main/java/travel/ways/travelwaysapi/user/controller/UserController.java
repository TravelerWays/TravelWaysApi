package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public List<UserResponse> GetAll() {
        return userService.getAll().stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @GetMapping("/logged")
    public UserResponse getLogged() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.getByUsername(auth.getName());

        return UserResponse.of(user);
    }
}
