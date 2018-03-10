package com.flytecnologia.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;

public class FlyUserDetails extends User {
    private static final long serialVersionUID = 1L;

    private FlyUser user;
    private Map<String, Object> additionalTokenInformation;

    public FlyUserDetails(FlyUser user,
                          Collection<? extends GrantedAuthority> authorities,
                          Map<String, Object> additionalTokenInformation) {
        super(user.getLogin(), user.getPassword(), authorities);
        this.user = user;
        this.additionalTokenInformation = additionalTokenInformation;
    }

    public FlyUser getUser() {
        return user;
    }

    public Map<String, Object> getAdditionalTokenInformation() {
        return additionalTokenInformation;
    }
}
