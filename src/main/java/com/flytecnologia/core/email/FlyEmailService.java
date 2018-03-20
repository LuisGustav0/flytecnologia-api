package com.flytecnologia.core.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class FlyEmailService {
    private JavaMailSender javaMailSender;

    FlyEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(FlyMessage flyMessage) throws MessagingException {
        /*SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(message.getFrom());
        simpleMailMessage.setSubject(message.getSubject());
        simpleMailMessage.setText(message.getText());


        if(message.getTo() != null){
            simpleMailMessage.setTo(message.getTo()
                    .toArray(new String[message.getTo().size()]));
        }

        if(message.getCc() != null){
            simpleMailMessage.setCc(message.getCc()
                    .toArray(new String[message.getCc().size()]));
        }

        if(message.getBcc() != null){
            simpleMailMessage.setBcc(message.getBcc()
                    .toArray(new String[message.getBcc().size()]));
        }



        javaMailSender.send(simpleMailMessage);*/


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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

        if (flyMessage.getFiles() != null) {
            flyMessage.getFiles().forEach((k,v) -> {
                try {
                    helper.addAttachment(k,v);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        if (flyMessage.getInputStreamSources() != null) {
            flyMessage.getInputStreamSources().forEach((k,v) -> {
                try {
                    helper.addAttachment(k,v);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        javaMailSender.send(message);
    }
}
