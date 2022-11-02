package travel.ways.travelwaysapi.mail.models.mailTemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecoveryPasswordTemplateModel {
    private String hash;
    private String frontAppUrl;
}