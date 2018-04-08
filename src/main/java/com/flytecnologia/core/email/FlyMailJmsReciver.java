package com.flytecnologia.core.email;

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
public class FlyMailJmsReciver {
    private FlyMailSender flyMailSender;

    FlyMailJmsReciver(FlyMailSender flyMailSender) {
        this.flyMailSender = flyMailSender;
    }

    private String from;
    private List<String> to;
    private List<String> bcc;
    private String subject;
    private String text;
    private Map<String, InputStreamSource> mapInputStream;

    @JmsListener(destination = "mailbox", containerFactory = "flyJmsFactory")
    public void receiveMessage(Message message) {
        try {
            MapMessage mapMessage = (MapMessage) message;

            FlyMailMessage flyMailMessage = new FlyMailMessage();
            flyMailMessage.setTo((List<String>) mapMessage.getObject("to"));
            flyMailMessage.setBcc((List<String>) mapMessage.getObject("bcc"));
            flyMailMessage.setCc((List<String>) mapMessage.getObject("cc"));
            flyMailMessage.setFrom(mapMessage.getString("from"));
            flyMailMessage.setText(mapMessage.getString("text"));
            flyMailMessage.setSubject(mapMessage.getString("subject"));

            List<String> attachmentFilenames = (List<String>) mapMessage.getObject("attachmentFilenames");

            Map<String, InputStreamSource> mapInputStream = new HashMap<>();

            if (attachmentFilenames != null) {
                for (String attachmentFilename : attachmentFilenames) {
                    InputStreamSource source = new ByteArrayResource(((byte[]) mapMessage.getObject(attachmentFilename)));

                    mapInputStream.put(attachmentFilename, source);
                }
            }

            flyMailMessage.setMapInputStream(mapInputStream);

            flyMailSender.sendSyncrono(flyMailMessage);
        } catch (MessagingException | JMSException ex) {
            ex.printStackTrace();
        }
    }
}
