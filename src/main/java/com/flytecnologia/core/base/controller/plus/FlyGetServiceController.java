package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.base.service.FlyService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public interface FlyGetServiceController<T extends FlyEntity, F extends FlyFilter> {
     FlyService<T, F> getService();
}
