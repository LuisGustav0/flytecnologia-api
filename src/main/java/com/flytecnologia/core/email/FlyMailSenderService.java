package com.flytecnologia.core.email;

import javax.mail.MessagingException;

public interface FlyMailSenderService {
    void send(FlyMailMessage flyMessage);
    void sendSync(FlyMailMessage flyMessage) throws MessagingException;
}
