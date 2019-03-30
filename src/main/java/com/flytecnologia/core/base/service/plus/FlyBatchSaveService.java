package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.List;

public interface FlyBatchSaveService<T extends FlyEntity, F extends FlyFilter> extends FlySaveService<T, F> {
    default void batchSave(List<T> entities) {
        batchSave(entities, 250);
    }

    default void batchSave(List<T> entities, int batchSize) {
        getRepository().batchSave(entities, batchSize);
    }

    default void batchSaveComplete(List<T> entities) {
        batchSaveComplete(entities, 250);
    }

    default void batchSaveComplete(List<T> entities, int batchSize) {
        entities.forEach(entity -> {
            validateBeforeCreate(entity);

            final boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

            if (!isIgnoreAfterSave) {
                afterSave(entity, null);
            }

            entity.setParameters(null);
        });

        getRepository().batchSave(entities, batchSize);
    }
}
