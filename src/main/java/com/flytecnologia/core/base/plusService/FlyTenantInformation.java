package com.flytecnologia.core.base.plusService;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.token.FlyTokenUserDetails;

public interface FlyTenantInformation extends FlyValidationBase {

    default String getTenant() {
        String tenantId = FlyTenantThreadLocal.getTenant();

        if (isEmpty(tenantId)) {
            tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + FlyTokenUserDetails.getCurrentSchemaName();
        }

        return tenantId;
    }

    default Long getUserId() {
        Long userId = FlyTenantThreadLocal.getUserId();

        if (isEmpty(userId))
            userId = FlyTokenUserDetails.getCurrentUserId();

        return userId;
    }
}
