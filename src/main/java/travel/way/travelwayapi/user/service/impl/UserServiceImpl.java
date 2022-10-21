package travel.way.travelwayapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.way.travelwayapi._core.models.Roles;
import travel.way.travelwayapi.user.models.db.AppUser;
import travel.way.travelwayapi.user.models.dto.request.CreateUserRequest;
import travel.way.travelwayapi.user.repository.RoleRepository;
import travel.way.travelwayapi.user.repository.UserRepository;
import travel.way.travelwayapi.user.shared.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AppUser createUser(CreateUserRequest request) {
        // ToDo: walidacja i sprawdzanie czy inny uzytkonik nie istnieje
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var user = AppUser.of(request);
        var role = roleRepository.findByName(Roles.ROLE_USER);
        user.getRoles().add(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public AppUser getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("user not found");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(x -> {
            authorities.add(new SimpleGrantedAuthority(x.getName()));
        });

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
