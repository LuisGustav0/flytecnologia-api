package com.flytecnologia.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class FlyUserDetails extends User {
    private static final long serialVersionUID = 1L;

    private FlyUser user;

    public FlyUserDetails(FlyUser user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getLogin(), user.getPassword(), authorities);
        this.user = user;
    }

    public FlyUser getUser() {
        return user;
    }
}
