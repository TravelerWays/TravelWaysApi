package travel.ways.travelwaysapi.mail.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import travel.ways.travelwaysapi._core.properity.MailProperty;
import travel.ways.travelwaysapi.mail.models.dto.request.SendMailRequest;

import javax.mail.internet.MimeMessage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @InjectMocks
    MailServiceImpl mailService;
    @Captor
    ArgumentCaptor<MimeMessage> argumentCaptor;
    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    MailProperty mailProperty;
    @Mock
    private JavaMailSender mailSender;
    @Autowired
    private JavaMailSender realMailSender;

    @BeforeEach
    void setUp() {
        mailService = new MailServiceImpl(mailSender, freeMarkerConfigurer, mailProperty);
    }

    //    @Autowired
    @Test
    public void sendMail_shouldSendMailWithTemplate() throws Exception {
        SendMailRequest sendMailRequest = new SendMailRequest(
                "subject",
                "test@example.com",
                "sample.ftl",
                null
        );

        when(mailSender.createMimeMessage()).thenReturn(realMailSender.createMimeMessage());
        mailProperty.setSend(true);
        doNothing().when(mailSender).send((MimeMessage) any());
        mailService.sendMail(sendMailRequest);
        verify(mailSender).send(argumentCaptor.capture());

        MimeMessageParser parser = new MimeMessageParser(argumentCaptor.getValue());

        String fileContent = FileUtils.readFileToString(new File(
                getClass().getClassLoader().getResource("mail-template/sample.ftl").toURI()
        ), "UTF-8");
        assertEquals(fileContent, parser.parse().getHtmlContent());

    }

    @Test
    public void sendMail_shouldNotSendMail_whenSendMailIsFalse() {
        SendMailRequest sendMailRequest = new SendMailRequest(
                "subject",
                "test@example.com",
                "sample.ftl",
                null
        );

        when(mailSender.createMimeMessage()).thenReturn(realMailSender.createMimeMessage());
        mailProperty.setSend(false);
        mailService.sendMail(sendMailRequest);
        verify(mailSender, Mockito.never()).send((MimeMessage) any());
    }

}