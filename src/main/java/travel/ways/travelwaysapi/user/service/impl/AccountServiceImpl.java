package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;
import travel.ways.travelwaysapi.mail.models.mailTemplate.ActiveAccountTemplateModel;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdatePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final UserService userService;
    private final CommonProperty commonProperty;

    @Override
    @SneakyThrows
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        var optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ServerException("User doesn't exists", HttpStatus.BAD_REQUEST);
        }
        var user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void chanePassword(UpdatePasswordRequest request) {
        var loggedUser = userService.getLoggedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), loggedUser.getPassword())) {
            throw new ServerException("Invalid old password", HttpStatus.BAD_REQUEST);
        }
        loggedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(loggedUser);
    }

    @Override
    @SneakyThrows
    public void updateUser(UpdateUserRequest request) {
        request.validAndThrowError();
        var loggedUser = userService.getLoggedUser();

        if(!loggedUser.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())){
            throw new ServerException("User with this email exists", HttpStatus.BAD_REQUEST);
        }

        loggedUser.setEmail(request.getEmail());
        loggedUser.setUsername(request.getEmail());
        loggedUser.setName(request.getName());
        loggedUser.setSurname(request.getSurname());

        userRepository.save(loggedUser);
    }

    @Override
    @SneakyThrows
    @Transactional
    public AppUser createUser(CreateUserRequest requestUser) {
        if (userRepository.existsByEmail(requestUser.getEmail())) {
            throw new ServerException("User with email: " + requestUser.getEmail() + " already exists",
                    HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(requestUser.getUsername())) {
            throw new ServerException("User with username: " + requestUser.getUsername() + " already exists",
                    HttpStatus.CONFLICT);
        }

        requestUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        AppUser user = AppUser.of(requestUser, List.of(roleRepository.findByName(Roles.ROLE_USER)));
        user.setHash(UUID.randomUUID().toString());

        user = userRepository.save(user);
        return user;
    }

    @Transactional
    @Override
    @SneakyThrows
    public AppUser activateUser(String hash) {
        AppUser user = userRepository.findByHash(hash);
        if (user == null) {
            throw new ServerException("Can't find user for hash", HttpStatus.NOT_FOUND);
        }
        user.setActive(true);
        return user;
    }

    public void sendActivationMail(AppUser user) {
        mailService.sendMail(new SendMailRequest<>(
                "Active account",
                user.getEmail(),
                "activeAccount.ftl",
                new ActiveAccountTemplateModel(user.getHash(), commonProperty.getFrontAppUrl())
        ));
    }
}
