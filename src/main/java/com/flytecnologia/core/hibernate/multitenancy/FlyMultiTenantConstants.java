package com.flytecnologia.core.hibernate.multitenancy;

public class FlyMultiTenantConstants {
    private FlyMultiTenantConstants() {}

    public static final String REQUEST_TOKEN_HEADER = "Authorization";
    public static final String REQUEST_HEADER_ID = "cl";
    public static final String REQUEST_HEADER_USER_ID = "userId";
    public static final String DEFAULT_TENANT_ID = "public";
    public static final String DEFAULT_TENANT_SUFFIX = "client_";

}
