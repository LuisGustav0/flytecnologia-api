package com.flytecnologia.core.base.service;

import com.flytecnologia.core.base.service.plus.*;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public interface FlyService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F>,
        FlyFindService<T, F>,
        FlyFindAllService<T, F>,
        FlySaveService<T, F>,
        FlyBatchSaveService<T, F>,
        FlyDeleteService<T, F>,
        FlyEntityInformationService<T, F>,
        FlySearchService<T, F>,
        FlyAutocompleteService<T, F>,
        FlyEntityReferenceService<T, F>,
        FlyFindNextService<T, F>,
        FlyGoToService<T, F>,
        FlyTenantService<T, F>,
        FlyFindValueService<T, F>,
        FlyDetachService<T, F>,
        FlyRecordCountService<T, F>,
        FlyInactiveService<T, F> {

}