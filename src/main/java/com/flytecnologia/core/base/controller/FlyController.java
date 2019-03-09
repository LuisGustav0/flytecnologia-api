package com.flytecnologia.core.base.controller;

import com.flytecnologia.core.base.controller.plus.FlyAutocompleteController;
import com.flytecnologia.core.base.controller.plus.FlyCreateController;
import com.flytecnologia.core.base.controller.plus.FlyDeleteController;
import com.flytecnologia.core.base.controller.plus.FlyFindByIdController;
import com.flytecnologia.core.base.controller.plus.FlyFindImageByIdController;
import com.flytecnologia.core.base.controller.plus.FlyGoToAfterController;
import com.flytecnologia.core.base.controller.plus.FlyGoToBeforeController;
import com.flytecnologia.core.base.controller.plus.FlySearchController;
import com.flytecnologia.core.base.controller.plus.FlyTodayController;
import com.flytecnologia.core.base.controller.plus.FlyUpdateController;
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
