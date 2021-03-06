package com.flytecnologia.core.user;

public interface FlyUser {
    Long getId();
    String getUsername();
    String getLogin();
    String getPassword();
    String getTenant();
    void setTenant(String tenant);
    String getEmail();
}
