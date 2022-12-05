package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi._core.util.TimeUtil;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;
import travel.ways.travelwaysapi.mail.models.mailTemplate.RecoveryPasswordTemplateModel;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.PasswordRecovery;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.repository.PasswordRecoveryRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private final UserRepository userRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final MailService mailService;
    private final CommonProperty commonProperty;


    @Override
    @SneakyThrows
    @Transactional
    public void initPasswordRecovery(InitPasswordRecoveryRequest request) {
        var user = userRepository.findByEmail(request.getEmail());
        if(user == null){
            throw new ServerException("User doesn't exists", HttpStatus.BAD_REQUEST);
        }

        passwordRecoveryRepository.setAllRecoveryAsUsed(user);

        var passwordRecoveryModel = new PasswordRecovery(
                UUID.randomUUID().toString(),
                false,
                TimeUtil.Now().addMinutes(20).getTimestamp(),
                user
        );

        mailService.sendMail(new SendMailRequest<>(
                "Password recovery",
                user.getEmail(),
                "passwordRecovery.ftl",
                new RecoveryPasswordTemplateModel(passwordRecoveryModel.getHash(), commonProperty.getFrontAppUrl())
        ));
        passwordRecoveryRepository.save(passwordRecoveryModel);
    }

    @Override
    public boolean isRecoveryHashValid(String hash) {
        var recoveryModel = passwordRecoveryRepository.findByHash(hash);
        if(recoveryModel == null){
            return false;
        }
        return !recoveryModel.isUsed() && recoveryModel.getExpiredAt().compareTo(TimeUtil.Now().getTimestamp()) > 0;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void setRecoveryHashAsUsed(String hash) {
        var recoveryModel = passwordRecoveryRepository.findByHash(hash);
        if(recoveryModel == null){
            throw new ServerException("invalid recovery hash", HttpStatus.BAD_REQUEST);
        }
        recoveryModel.setUsed(true);
    }

    @Override
    public AppUser getUserByRecoveryHash(String hash) {
        return passwordRecoveryRepository.findByHash(hash).getUser();
    }
}
