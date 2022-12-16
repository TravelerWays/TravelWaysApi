package travel.ways.travelwaysapi.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi._core.util.Time;
import travel.ways.travelwaysapi._core.util.impl.TimeImpl;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.PasswordRecovery;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.repository.PasswordRecoveryRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordRecoveryServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordRecoveryRepository passwordRecoveryRepository;
    @Mock
    private MailService mailService;
    @Mock
    private Time time;

    private final CommonProperty commonProperty = new CommonProperty("frontApi");

    private PasswordRecoveryServiceImpl passwordRecoveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(time.now()).thenReturn(new TimeImpl());
        passwordRecoveryService = new PasswordRecoveryServiceImpl(userRepository, passwordRecoveryRepository, mailService, commonProperty, time);
    }

    @Test
    public void initPasswordRecovery_whenUserDoesntExist_throwError() {
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(null);

        assertThrows(ServerException.class, () -> passwordRecoveryService.initPasswordRecovery(new InitPasswordRecoveryRequest("email")));
    }

    @Test
    public void initPasswordRecovery_whenUserExists_thenSendMail() {
        // arrange
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(new AppUser(
                "Jhon",
                "Doe",
                "JD",
                "elo",
                "email@gmail.com",
                List.of(new Role("ROLE_USER"))
        ));

        // act
        passwordRecoveryService.initPasswordRecovery(new InitPasswordRecoveryRequest("test@test.com"));

        // assert
        Mockito.verify(mailService, Mockito.times(1)).sendMail(Mockito.any());
    }

    @Test
    public void isRecoveryHashValid_whenIsInvalid_ReturnFalse() {

        // arrange
        Mockito.when(passwordRecoveryRepository.findByHash(Mockito.any())).thenReturn(new PasswordRecovery("hash", true, new TimeImpl().now().addMinutes(-30).getTimestamp(), null));

        // act
        var result = passwordRecoveryService.isRecoveryHashValid("hash");

        // assert
        assertFalse(result);
    }

    @Test
    public void isRecoveryHashValid_whenIsValid_ReturnTrue() {
        // arrange
        Mockito.when(passwordRecoveryRepository.findByHash(Mockito.any())).thenReturn(new PasswordRecovery("hash", false, new TimeImpl().now().addMinutes(2).getTimestamp(), null));

        // act
        var result = passwordRecoveryService.isRecoveryHashValid("hash");

        // assert
        assertTrue(result);
    }

}