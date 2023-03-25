package travel.ways.travelwaysapi.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.properity.CommonProperty;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.repository.RoleRepository;
import travel.ways.travelwaysapi.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;


class AccountServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private MailService mailService;
    @Mock
    private CommonProperty commonProperty;

    private AccountServiceImpl accountManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountManager = new AccountServiceImpl(userRepository, passwordEncoder, roleRepository, mailService, commonProperty);
    }

    @Test
    public void changePassword_whenUserExists_changePassword() {
        // arrange
        var mockAppUser = Mockito.mock(AppUser.class);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockAppUser));

        // act
        accountManager.changePassword(1L, "123");

        // assert
        Mockito.verify(mockAppUser, Mockito.times(1)).setPassword(any());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(any());
    }

    @Test
    public void registerUser_whenUserExists_throwException() {
        CreateUserRequest userRequest = new CreateUserRequest(
                "testName",
                "testSurname",
                "testUsername",
                "passwd",
                "test@mail.com"
        );
        Mockito.when(userRepository.existsByEmail(any())).thenReturn(true);
        Exception exception = assertThrows(ServerException.class, () -> accountManager.createUser(userRequest));
        assertTrue(exception.getMessage().contains("already exists"));
    }
}