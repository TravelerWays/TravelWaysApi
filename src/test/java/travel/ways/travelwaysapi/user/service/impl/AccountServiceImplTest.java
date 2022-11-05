package travel.ways.travelwaysapi.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.repository.UserRepository;

import java.util.Optional;

class AccountServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountServiceImpl accountManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountManager = new AccountServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    public void changePassword_whenUserExists_changePassword() {
        // arrange
        var mockAppUser = Mockito.mock(AppUser.class);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockAppUser));

        // act
        accountManager.changePassword(1L, "123");

        // assert
        Mockito.verify(mockAppUser, Mockito.times(1)).setPassword(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.any());
    }
}