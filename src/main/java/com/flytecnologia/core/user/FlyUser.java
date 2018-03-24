package com.flytecnologia.core.user;

public interface FlyUser {
    public Long getId();
    public String getUsername();
    public String getLogin();
    public String getPassword();
    public String getTenant();
    public String getEmail();
}
