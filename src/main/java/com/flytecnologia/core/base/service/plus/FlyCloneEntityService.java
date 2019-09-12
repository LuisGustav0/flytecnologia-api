package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class FlyCloneEntityService {
    private FlyCloneEntityService(){}

    public static <T extends FlyEntity> T cloneEntity(T entitySaved) {
        FlyEntity oldEntity = null;
        try {
            oldEntity = entitySaved.getClass().newInstance();
            BeanUtils.copyProperties(entitySaved, oldEntity);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return (T) oldEntity;
    }
}
