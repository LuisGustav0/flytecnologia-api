package com.flytecnologia.core.email;

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

@Component
public class FlyMailSender {
    private JavaMailSender javaMailSender;
    private JmsTemplate jmsTemplate;

    FlyMailSender(JavaMailSender javaMailSender,
                  JmsTemplate jmsTemplate) {
        this.javaMailSender = javaMailSender;
        this.jmsTemplate = jmsTemplate;
    }

    public void send(FlyMailMessage flyMessage) {
        MessageCreator messageCreator = session -> {
            MapMessage message = session.createMapMessage();

            message.setString("from", flyMessage.getFrom());
            message.setObject("to", flyMessage.getTo());
            message.setObject("bcc", flyMessage.getBcc());
            message.setObject("cc", flyMessage.getCc());
            message.setString("subject", flyMessage.getSubject());
            message.setString("text", flyMessage.getText());

            List<String> attachmentFilenames = new ArrayList<>() ;

            if(flyMessage.getMapInputStream() != null) {
                flyMessage.getMapInputStream().forEach((attachmentFilename, inputStreamSource) -> {
                    try {
                        message.setObject(attachmentFilename,  IOUtils.toByteArray(inputStreamSource.getInputStream()));

                        attachmentFilenames.add(attachmentFilename);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            message.setObject("attachmentFilenames", attachmentFilenames);

            return message;
        };

        jmsTemplate.send("mailbox", messageCreator);
    }

    protected void sendSyncrono(FlyMailMessage flyMessage) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(flyMessage.getSubject());
        helper.setText(flyMessage.getText());
        helper.setFrom(new InternetAddress(flyMessage.getFrom()));

        if (flyMessage.getTo() != null) {
            helper.setTo(flyMessage.getTo()
                    .toArray(new String[flyMessage.getTo().size()]));
        }

        if (flyMessage.getCc() != null) {
            helper.setCc(flyMessage.getCc()
                    .toArray(new String[flyMessage.getCc().size()]));
        }

        if (flyMessage.getBcc() != null) {
            helper.setBcc(flyMessage.getBcc()
                    .toArray(new String[flyMessage.getBcc().size()]));
        }

        if (flyMessage.getMapInputStream() != null) {
            flyMessage.getMapInputStream().forEach((attachmentFilename, inputStreamSource) -> {
                try {
                    helper.addAttachment(attachmentFilename, inputStreamSource);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        javaMailSender.send(helper.getMimeMessage());
    }
}
