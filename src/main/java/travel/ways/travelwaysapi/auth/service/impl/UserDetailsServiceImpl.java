package travel.ways.travelwaysapi.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi.auth.model.CustomUserDetails;
import travel.ways.travelwaysapi.user.service.shared.UserService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        var user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
            return CustomUserDetails.build(user);
        }
}