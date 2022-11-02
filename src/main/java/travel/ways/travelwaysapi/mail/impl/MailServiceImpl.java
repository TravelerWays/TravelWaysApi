package travel.ways.travelwaysapi.mail.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import travel.ways.travelwaysapi._core.properity.MailProperty;
import travel.ways.travelwaysapi.mail.MailService;
import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;
    private final MailProperty mailProperty;

    @Override
    @SneakyThrows
    public void sendMail(SendMailRequest sendMailRequest) {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(mailProperty.getFrom());
        helper.setTo(sendMailRequest.getTo());
        helper.setSubject(sendMailRequest.getSubject());;

        var freemarkerTemplate = freemarkerConfigurer.getConfiguration()
                .getTemplate(sendMailRequest.getTemplate());

        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, sendMailRequest.getProperties());

        if(mailProperty.isSend()) {
            helper.setText(htmlBody, true);
            mailSender.send(message);
        }else{
            log.info("mail send {}", htmlBody);
        }
    }
}