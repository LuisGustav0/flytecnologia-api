package com.flytecnologia.core.base.service.plus;

import java.util.Base64;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public class FlyBase64Service {
    private FlyBase64Service() {
    }

    public static String removeBase64Information(String encode) {
        if (isEmpty(encode))
            return encode;

        final int indexOf = encode.indexOf(";base64,");

        if (indexOf <= 0)
            return encode;

        return encode.substring(indexOf + 8);
    }

    public static String convertToBase64(byte[] data) {
        if (data == null)
            return null;

        return new String(Base64.getEncoder().encode(data));
    }

}
