package com.flytecnologia.core.base.service.plus;

import java.util.HashMap;
import java.util.Map;

public class FlyMapParameterService {
    public static Map<String, Object> getMapParameter(String key, Object value) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put(key, value);
        return parameter;
    }
}
