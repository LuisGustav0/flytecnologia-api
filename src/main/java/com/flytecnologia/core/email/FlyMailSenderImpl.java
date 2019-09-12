package com.flytecnologia.core.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FlyMailSenderImpl implements FlyMailSenderService{
    private JavaMailSender javaMailSender;
    private JmsTemplate jmsTemplate;

    FlyMailSenderImpl(JavaMailSender javaMailSender,
                      JmsTemplate jmsTemplate) {
        this.javaMailSender = javaMailSender;
        this.jmsTemplate = jmsTemplate;
    }

    public void send(FlyMailMessage flyMessage) {
        final MessageCreator messageCreator = session -> {
            final MapMessage message = session.createMapMessage();

            message.setString("from", flyMessage.getFrom());
            message.setObject("to", flyMessage.getTo());
            message.setObject("bcc", flyMessage.getBcc());
            message.setObject("cc", flyMessage.getCc());
            message.setString("subject", flyMessage.getSubject());
            message.setString("text", flyMessage.getText());

            final List<String> attachmentFilenames = new ArrayList<>() ;

            if(flyMessage.getMapInputStream() != null) {
                flyMessage.getMapInputStream().forEach((attachmentFilename, inputStreamSource) -> {
                    try {
                        message.setObject(attachmentFilename,  IOUtils.toByteArray(inputStreamSource.getInputStream()));

                        attachmentFilenames.add(attachmentFilename);
                    } catch  (JMSException | IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
            }

            message.setObject("attachmentFilenames", attachmentFilenames);

            return message;
        };

        jmsTemplate.send("mailbox", messageCreator);
    }

    public void sendSync(FlyMailMessage flyMessage) throws MessagingException {
        final MimeMessage message = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(flyMessage.getSubject());
        helper.setText(flyMessage.getText());
        helper.setFrom(new InternetAddress(flyMessage.getFrom()));

        if (flyMessage.getTo() != null) {
            helper.setTo(flyMessage.getTo()
                    .toArray(new String[0]));
        }

        if (flyMessage.getCc() != null) {
            helper.setCc(flyMessage.getCc()
                    .toArray(new String[0]));
        }

        if (flyMessage.getBcc() != null) {
            helper.setBcc(flyMessage.getBcc()
                    .toArray(new String[0]));
        }

        if (flyMessage.getMapInputStream() != null) {
            flyMessage.getMapInputStream().forEach((attachmentFilename, inputStreamSource) -> {
                try {
                    helper.addAttachment(attachmentFilename, inputStreamSource);
                } catch (MessagingException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }

        javaMailSender.send(helper.getMimeMessage());
    }
}
