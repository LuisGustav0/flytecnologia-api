package com.flytecnologia.core.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class FlyMailJmsReciverServiceImpl implements FlyMailJmsReciverService{
    private FlyMailSenderService mailSenderService;

    @JmsListener(destination = "mailbox", containerFactory = "flyJmsFactory")
    public void receiveMessage(Message message) {
        try {
            final MapMessage mapMessage = (MapMessage) message;

            FlyMailMessage flyMailMessage = new FlyMailMessage();
            flyMailMessage.setTo((List<String>) mapMessage.getObject("to"));
            flyMailMessage.setBcc((List<String>) mapMessage.getObject("bcc"));
            flyMailMessage.setCc((List<String>) mapMessage.getObject("cc"));
            flyMailMessage.setFrom(mapMessage.getString("from"));
            flyMailMessage.setText(mapMessage.getString("text"));
            flyMailMessage.setSubject(mapMessage.getString("subject"));

            final List<String> attachmentFilenames = (List<String>) mapMessage.getObject("attachmentFilenames");

            final Map<String, InputStreamSource> mapInputStream = new HashMap<>();

            if (attachmentFilenames != null) {
                for (String attachmentFilename : attachmentFilenames) {
                    InputStreamSource source = new ByteArrayResource(((byte[]) mapMessage.getObject(attachmentFilename)));

                    mapInputStream.put(attachmentFilename, source);
                }
            }

            flyMailMessage.setMapInputStream(mapInputStream);

            mailSenderService.sendSync(flyMailMessage);
        } catch (MessagingException | JMSException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
