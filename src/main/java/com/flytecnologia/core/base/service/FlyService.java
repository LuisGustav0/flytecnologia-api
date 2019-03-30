package com.flytecnologia.core.base.service;

import com.flytecnologia.core.base.service.plus.FlyAutocompleteService;
import com.flytecnologia.core.base.service.plus.FlyBatchSaveService;
import com.flytecnologia.core.base.service.plus.FlyDeleteService;
import com.flytecnologia.core.base.service.plus.FlyDetachService;
import com.flytecnologia.core.base.service.plus.FlyEntityInformationService;
import com.flytecnologia.core.base.service.plus.FlyEntityReferenceService;
import com.flytecnologia.core.base.service.plus.FlyFindAllService;
import com.flytecnologia.core.base.service.plus.FlyFindNextService;
import com.flytecnologia.core.base.service.plus.FlyFindService;
import com.flytecnologia.core.base.service.plus.FlyFindValueService;
import com.flytecnologia.core.base.service.plus.FlyGetRepositoryService;
import com.flytecnologia.core.base.service.plus.FlyGoToService;
import com.flytecnologia.core.base.service.plus.FlyInactiveService;
import com.flytecnologia.core.base.service.plus.FlyRecordCountService;
import com.flytecnologia.core.base.service.plus.FlySaveService;
import com.flytecnologia.core.base.service.plus.FlySearchService;
import com.flytecnologia.core.base.service.plus.FlyTenantService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> implements
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