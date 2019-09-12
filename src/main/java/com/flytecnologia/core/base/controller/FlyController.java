package com.flytecnologia.core.base.controller;

import com.flytecnologia.core.base.controller.plus.*;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public abstract class FlyController<T extends FlyEntity, F extends FlyFilter> implements
        FlyCreateController<T, F>,
        FlyGoToAfterController<T, F>,
        FlyGoToBeforeController<T, F>,
        FlyFindByIdController<T, F>,
        FlyDeleteController<T, F>,
        FlyFindImageByIdController<T, F>,
        FlyUpdateController<T, F>,
        FlyAutocompleteController<T, F>,
        FlyTodayController<T, F>,
        FlySearchController<T, F> {
}
