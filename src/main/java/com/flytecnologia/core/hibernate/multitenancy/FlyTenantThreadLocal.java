package com.flytecnologia.core.hibernate.multitenancy;

import java.util.HashMap;
import java.util.Map;

public class FlyTenantThreadLocal {

    private static final ThreadLocal<Map<String, Object>> tenantThreadLocal = new ThreadLocal<>();

    public static void remove() {
        tenantThreadLocal.remove();
    }

    public static String getTenant() {
        if (tenantThreadLocal.get() == null) {
            return null;
        }

        return (String) tenantThreadLocal.get().get("tenant");
    }

    public static void setTenant(String tenantCode) {
        if (tenantThreadLocal.get() == null) {
            tenantThreadLocal.set(new HashMap<>());
        }

        tenantThreadLocal.get().put("tenant", tenantCode);
    }

    public static Long getUserId() {
        if (tenantThreadLocal.get() == null) {
            return null;
        }

        return (Long) tenantThreadLocal.get().get("userId");
    }

    public static void setUserId(Long userId) {
        if (tenantThreadLocal.get() == null) {
            tenantThreadLocal.set(new HashMap<>());
        }

        tenantThreadLocal.get().put("userId", userId);
    }
}
