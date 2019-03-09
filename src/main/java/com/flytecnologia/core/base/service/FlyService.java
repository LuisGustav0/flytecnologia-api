package com.flytecnologia.core.base.service;

import com.flytecnologia.core.base.service.plus.FlyAutocompleteService;
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
import com.flytecnologia.core.base.service.plus.FlyPrintService;
import com.flytecnologia.core.base.service.plus.FlyTenantInformationService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> implements
        FlyPrintService<F>,
        FlyGetRepositoryService<T, F>,
        FlyFindService<T, F>,
        FlyFindAllService<T, F>,
        FlyTenantInformationService.FlySaveService<T, F>,
        FlyDeleteService<T, F>,
        FlyEntityInformationService<T, F>,
        FlyTenantInformationService.FlySearchService<T, F>,
        FlyAutocompleteService<T, F>,
        FlyEntityReferenceService<T, F>,
        FlyFindNextService<T, F>,
        FlyGoToService<T, F>,
        FlyTenantInformationService.FlyTenantService<T, F>,
        FlyFindValueService<T, F>,
        FlyDetachService<T, F>,
        FlyTenantInformationService.FlyRecordCountService<T, F>,
        FlyInactiveService<T, F> {

}