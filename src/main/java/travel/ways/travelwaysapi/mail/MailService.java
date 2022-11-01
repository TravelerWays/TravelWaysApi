package travel.ways.travelwaysapi.mail;

import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;

public interface MailService {
    void sendMail(SendMailRequest sendMailRequest);
}
