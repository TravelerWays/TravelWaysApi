package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.internal.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @SneakyThrows
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        var optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new ServerException("User doesn't exists", HttpStatus.BAD_REQUEST.value());
        }
        var user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void registerUser(CreateUserRequest requestUser) {
        if(userRepository.findByEmail(requestUser.getEmail()) != null){
            throw new ServerException("User with email: " + requestUser.getEmail() + " already exists.",
                    HttpStatus.CONFLICT.value());
        }
        if(userRepository.findByUsername(requestUser.getUsername()) != null){
            throw new ServerException("User with username: " + requestUser.getEmail() + " already exists.",
                    HttpStatus.CONFLICT.value());
        }
        requestUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));

        AppUser user = AppUser.of(requestUser);
        Role role = roleRepository.findByName(Roles.ROLE_USER);
        user.getRoles().add(role);
        userRepository.save(user);
    }


}
