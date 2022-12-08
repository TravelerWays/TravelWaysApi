package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.ChangePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.model.dto.response.ValidHashPasswordRecoveryResponse;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;
import travel.ways.travelwaysapi.user.service.shared.AccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final PasswordRecoveryService recoveryPasswordService;
    private final AccountService accountService;

    @PostMapping("/register")
    public BaseResponse registerUser( @RequestBody @Valid CreateUserRequest createUserRequest) {
        AppUser user = accountService.createUser(createUserRequest);
        accountService.sendActivationMail(user);
        return new BaseResponse(true, "user registered");
    }

    @GetMapping("/activate/{hash}")
    public BaseResponse activateAccount(@PathVariable String hash){
        accountService.activateUser(hash);
        return new BaseResponse(true, "user active");
    }


    @PostMapping("password-recovery/init")
    public BaseResponse passwordRecoverInit(@Valid @RequestBody InitPasswordRecoveryRequest request) {
        recoveryPasswordService.initPasswordRecovery(request);
        return new BaseResponse(true, "mail with link sent");
    }

    @SneakyThrows
    @GetMapping("password-recovery/valid/{hash}")
    public ValidHashPasswordRecoveryResponse validPasswordRecovery(@PathVariable String hash) {
        var isValid = recoveryPasswordService.isRecoveryHashValid(hash);
        if(!isValid) throw new ServerException("Invalid hash", HttpStatus.BAD_REQUEST);
        return new ValidHashPasswordRecoveryResponse(isValid);
    }

    @SneakyThrows
    @PostMapping("password-recovery/change-password/{hash}")
    public BaseResponse changePassword(@Valid @RequestBody ChangePasswordRequest request, @PathVariable String hash) {
        if (!recoveryPasswordService.isRecoveryHashValid(hash)) {
            throw new ServerException("Invalid hash", HttpStatus.BAD_REQUEST);
        }
        var user = recoveryPasswordService.getUserByRecoveryHash(hash);
        accountService.changePassword(user.getId(), request.getPassword());
        recoveryPasswordService.setRecoveryHashAsUsed(hash);

        return new BaseResponse(true, "password changed");
    }
}
