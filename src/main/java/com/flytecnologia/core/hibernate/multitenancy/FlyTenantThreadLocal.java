package com.flytecnologia.core.hibernate.multitenancy;

public class FlyTenantThreadLocal {
    private static final ThreadLocal<String> tenantThreadLocal = new ThreadLocal<String>();

    public static String getTenant() {
        return tenantThreadLocal.get();
    }

    public static void setTenant(String tenantCode) {
        tenantThreadLocal.set(tenantCode);
    }
}
