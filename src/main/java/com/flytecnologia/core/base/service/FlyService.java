package com.flytecnologia.core.base.service;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> implements IFlyService<T, F> {
}