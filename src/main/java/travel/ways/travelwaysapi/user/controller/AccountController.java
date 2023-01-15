package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("User " + createUserRequest.getUsername() + " registered successfully");
        return new BaseResponse(true, "user registered");
    }

    @PostMapping("/activate/{activateHash}")
    public BaseResponse activateAccount(@PathVariable String activateHash){
        AppUser user = accountService.activateUser(activateHash);
        log.info("User " + user.getUsername() + " activated successfully");
        return new BaseResponse(true, "user activated");
    }


    @PostMapping("password-recovery/init")
    public BaseResponse passwordRecoverInit(@Valid @RequestBody InitPasswordRecoveryRequest request) {
        recoveryPasswordService.initPasswordRecovery(request);
        log.info("Mail with recovery password link has been sent to " + request.getEmail());
        return new BaseResponse(true, "mail with link sent");
    }

    @SneakyThrows
    @GetMapping("password-recovery/valid/{refactorHash}")
    public ValidHashPasswordRecoveryResponse validPasswordRecovery(@PathVariable String refactorHash) {
        var isValid = recoveryPasswordService.isRecoveryHashValid(refactorHash);
        if(!isValid) throw new ServerException("Invalid hash", HttpStatus.BAD_REQUEST);
        log.debug("recovery hash is valid");
        return new ValidHashPasswordRecoveryResponse(true);
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
        log.info("password for user: " + user + " has been changed");
        return new BaseResponse(true, "password changed");
    }
}
