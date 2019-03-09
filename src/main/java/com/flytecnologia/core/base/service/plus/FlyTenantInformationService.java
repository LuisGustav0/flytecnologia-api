package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.token.FlyTokenUserDetails;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isNotEmpty;

public class FlyTenantInformationService {

    public static String getTenant() {
        String tenantId = FlyTenantThreadLocal.getTenant();

        if (isEmpty(tenantId)) {
            String currentSchemaName = FlyTokenUserDetails.getCurrentSchemaName();

            if (isNotEmpty(currentSchemaName)) {
                tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + currentSchemaName;
            } else {
                tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_ID;
            }
        }

        return tenantId;
    }

    public static Long getUserId() {
        Long userId = FlyTenantThreadLocal.getUserId();

        if (isEmpty(userId))
            userId = FlyTokenUserDetails.getCurrentUserId();

        return userId;
    }
}
