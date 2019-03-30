package com.flytecnologia.core.email;

import javax.jms.Message;

public interface FlyMailJmsReciverService {
    void receiveMessage(Message message);
}
