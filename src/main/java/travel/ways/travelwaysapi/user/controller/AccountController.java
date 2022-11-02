package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.user.model.dto.request.ChangePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;
import travel.ways.travelwaysapi.user.model.dto.response.ValidHashPasswordRecoveryResponse;
import travel.ways.travelwaysapi.user.service.internal.AccountManager;
import travel.ways.travelwaysapi.user.service.internal.PasswordRecoveryService;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final PasswordRecoveryService recoveryPasswordService;
    private final AccountManager accountManager;

    @PostMapping("password-recovery/init")
    public BaseResponse passwordRecoverInit(@RequestBody InitPasswordRecoveryRequest request) {
        recoveryPasswordService.initPasswordRecovery(request);
        return new BaseResponse(true, "mail with link send");
    }

    @GetMapping("password-recovery/valid/{hash}")
    public ValidHashPasswordRecoveryResponse validPasswordRecovery(@PathVariable String hash) {
        var isValid = recoveryPasswordService.isRecoveryHashValid(hash);
        return new ValidHashPasswordRecoveryResponse(isValid);
    }

    @PostMapping("password-recovery/change-password/{hash}")
    public BaseResponse changePassword(@RequestBody ChangePasswordRequest request, @PathVariable String hash) {
        if (!recoveryPasswordService.isRecoveryHashValid(hash)) {
            return new BaseResponse(false, "invalid recovery hash");
        }

        var user = recoveryPasswordService.getUserByRecoveryHash(hash);
        accountManager.changePassword(user.getId(), request.getPassword());
        recoveryPasswordService.setRecoveryHashAsUsed(hash);

        return new BaseResponse(true, "password changed");
    }
}
