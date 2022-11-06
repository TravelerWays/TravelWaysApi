package travel.ways.travelwaysapi.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.model.dto.request.ChangePasswordRequest;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {
    @Mock
    private PasswordRecoveryService passwordRecoveryService;
    @Mock
    private AccountService accountService;

    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountController = new AccountController(passwordRecoveryService, accountService);
    }


    @Test
    public void changePassword_whenInvalidRecoveryHash_thenReturnFail(){
        // arrange
        Mockito.when(passwordRecoveryService.isRecoveryHashValid(Mockito.any())).thenReturn(false);

        // act
        var result = accountController.changePassword(new ChangePasswordRequest("test"), "hash");

        // assert
        assertFalse(result.isSuccess());
        assertSame("invalid recovery hash", result.getMessage());
    }

    @Test
    public void changePassword_whenValidRecoveryHash_thenReturnSuccess(){
        // arrange
        var appUser = new AppUser(
                "Jhon",
                "Doe",
                "JD",
                "elo",
                "email@gmail.com",
                List.of(new Role("ROLE_USER"))
        );
        appUser.setId(2L);
        Mockito.when(passwordRecoveryService.isRecoveryHashValid(Mockito.any())).thenReturn(true);
        Mockito.when(passwordRecoveryService.getUserByRecoveryHash(Mockito.any())).thenReturn(appUser);
        // act
        var result = accountController.changePassword(new ChangePasswordRequest("test"), "hash");

        // assert
        assertTrue(result.isSuccess());
        assertSame("password changed", result.getMessage());
        Mockito.verify(accountService, Mockito.times(1)).changePassword(2L, "test");
    }
}