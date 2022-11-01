package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.util.TimeUtil;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.PasswordRecovery;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.repository.PasswordRecoveryRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;

import java.sql.Timestamp;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private final UserRepository userRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;


    @Override
    @SneakyThrows
    @Transactional
    public String initRecoveryPassword(InitPasswordRecoveryRequest request) {
        var user = userRepository.findByEmail(request.getEmail());
        if(user == null){
            throw new ServerException("User doesn't exists", HttpStatus.BAD_REQUEST.value());
        }
        //todo: send mail
        var now = TimeUtil.Now();

        passwordRecoveryRepository.setAllRecoveryAsUsed(user);
        var passwordRecoveryModel = new PasswordRecovery(
                UUID.randomUUID().toString(),
                false,
                new Timestamp(TimeUtil.AddMinutes(now, 20).getTime()),
                user
        );

        passwordRecoveryRepository.save(passwordRecoveryModel);
        return passwordRecoveryModel.getHash();
    }

    @Override
    public boolean isRecoveryHashValid(String hash) {
        var recoveryModel = passwordRecoveryRepository.findByHash(hash);
        if(recoveryModel == null){
            return false;
        }
        var now = TimeUtil.NowTimestamp();

        return !recoveryModel.isUsed() && recoveryModel.getExpiredAt().compareTo(now) > 0;
    }

    @Override
    public AppUser getUserByRecoveryHash(String hash) {
        return passwordRecoveryRepository.findByHash(hash).getUser();
    }
}
