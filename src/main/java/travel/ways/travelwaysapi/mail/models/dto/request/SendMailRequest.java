package travel.ways.travelwaysapi.mail.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class SendMailRequest<T> {
    private String subject;
    private String to;
    private String template;
    private T properties;
}
