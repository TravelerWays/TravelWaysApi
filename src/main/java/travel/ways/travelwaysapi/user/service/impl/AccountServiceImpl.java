package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.Roles;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;
import travel.ways.travelwaysapi.mail.models.mailTemplate.ActiveAccountTemplateModel;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.shared.AccountService;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final CommonProperty commonProperty;

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
        if(userRepository.existsByEmail(requestUser.getEmail())){
            throw new ServerException("User with email: " + requestUser.getEmail() + " already exists.",
                    HttpStatus.CONFLICT.value());
        }
        if(userRepository.existsByUsername(requestUser.getUsername())){
            throw new ServerException("User with username: " + requestUser.getEmail() + " already exists.",
                    HttpStatus.CONFLICT.value());
        }

        requestUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));

        AppUser user = AppUser.of(requestUser);
        Role role = roleRepository.findByName(Roles.ROLE_USER);
        user.getRoles().add(role);
        user.setHash(UUID.randomUUID().toString());

        mailService.sendMail(new SendMailRequest<ActiveAccountTemplateModel>(
                "Active account",
                user.getEmail(),
                "activeAccount.ftl",
                new ActiveAccountTemplateModel(user.getHash(), commonProperty.getFrontAppUrl())
        ));

        userRepository.save(user);
    }

    @Transactional
    @Override
    @SneakyThrows
    public void confirmUser(String hash){
        AppUser user = userRepository.findByHash(hash);
        if(user == null){
            throw new ServerException("Can't find user for hash", HttpStatus.NOT_FOUND.value());
        }
        user.setActive(true);
    }


}
