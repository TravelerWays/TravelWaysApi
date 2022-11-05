package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.internal.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public void registerUser(CreateUserRequest createUserRequest) {

    }


}
