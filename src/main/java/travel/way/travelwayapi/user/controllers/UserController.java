package travel.way.travelwayapi.user.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.way.travelwayapi.user.models.dto.response.UserDto;
import travel.way.travelwayapi.user.shared.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("all")
    public List<UserDto> GetAll() {
        return userService.getAll().stream().map(UserDto::of).collect(Collectors.toList());
    }
}
