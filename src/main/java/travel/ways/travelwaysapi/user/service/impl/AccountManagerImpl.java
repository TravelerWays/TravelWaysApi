package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.user.repository.PasswordRecoveryRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.internal.AccountManager;

@Service
@RequiredArgsConstructor
public class AccountManagerImpl implements AccountManager {
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
}
