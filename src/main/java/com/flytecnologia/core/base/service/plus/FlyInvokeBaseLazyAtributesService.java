package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Basic;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
public class FlyInvokeBaseLazyAtributesService {
    private FlyInvokeBaseLazyAtributesService() {}

    public static <T extends FlyEntity> void invokeBaseLazyAtributesService(T entity) {
        final Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();

            if (annotations != null && annotations.length > 0) {
                String name = field.getName();

                for (Annotation annotation : annotations) {
                    if (annotation instanceof Basic) {
                        try {
                            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

                            Method method = entity.getClass().getDeclaredMethod(methodName);

                            method.invoke(entity);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
