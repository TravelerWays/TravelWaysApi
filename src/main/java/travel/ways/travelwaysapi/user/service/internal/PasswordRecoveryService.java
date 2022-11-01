package travel.ways.travelwaysapi.user.service.internal;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.InitPasswordRecoveryRequest;

public interface PasswordRecoveryService {
    String initRecoveryPassword(InitPasswordRecoveryRequest request);
    boolean isRecoveryHashValid(String hash);
    void setRecoveryHashAsUsed(String hash);
    AppUser getUserByRecoveryHash(String hash);
}
