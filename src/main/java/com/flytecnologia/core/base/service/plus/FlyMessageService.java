package com.flytecnologia.core.base.service.plus;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlyMessageService {
    private MessageSource messageSource;

    public String getMessage(String field) {
        return messageSource.getMessage(field, null, LocaleContextHolder.getLocale());
    }
}
