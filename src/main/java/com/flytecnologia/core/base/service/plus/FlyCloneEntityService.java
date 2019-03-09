package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import org.springframework.beans.BeanUtils;

public class FlyCloneEntityService {
    public static <T extends FlyEntity> T cloneEntity(T entitySaved) {
        FlyEntity oldEntity = null;
        try {
            oldEntity = entitySaved.getClass().newInstance();
            BeanUtils.copyProperties(entitySaved, oldEntity);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) oldEntity;
    }
}
