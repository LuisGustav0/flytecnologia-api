package com.flytecnologia.core.hibernate.multitenancy;

import java.util.Map;

public class FlyTenantThreadLocal {
    private static final ThreadLocal<Map<String, Object>> tenantThreadLocal = new ThreadLocal<>();

    public static String getTenant() {
        return (String) tenantThreadLocal.get().get("tenant");
    }

    public static void setTenant(String tenantCode) {
        tenantThreadLocal.get().put("tenant", tenantCode);
    }

    public static Long getUserId() {
        return (Long) tenantThreadLocal.get().get("user");
    }

    public static void setUserId(Long userId) {
        tenantThreadLocal.get().put("userId", userId);
    }
}
