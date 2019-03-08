package com.flytecnologia.core.base.service.plus;

import java.util.Base64;

public interface FlyBase64Service extends FlyValidationService {
    default String removeBase64Information(String encode) {
        if (isEmpty(encode))
            return encode;

        final int indexOf = encode.indexOf(";base64,");

        if (indexOf <= 0)
            return encode;

        return encode.substring(indexOf + 8);
    }

    default String convertToBase64(byte[] data) {
        if (data == null)
            return null;

        return new String(Base64.getEncoder().encode(data));
    }

}