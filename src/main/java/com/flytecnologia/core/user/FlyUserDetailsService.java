package com.flytecnologia.core.user;

import com.flytecnologia.core.token.FlyTokenUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class FlyUserDetailsService implements UserDetailsService {

    @Autowired
    private FlyUserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        String loginInvalid = userService.getMessageInvalidLogin();

        Optional<FlyUser> user = userService.findByLoginOrEmail(login);

        FlyUser flyUser = user.orElseThrow(() -> new UsernameNotFoundException(loginInvalid));

        Map<String, Object> additionalTokenInformation = userService.getAdditionalTokenInformation(flyUser, login, flyUser.getId());

        return new FlyUserDetails(flyUser,
                getAuthorities(login, flyUser.getTenant(), loginInvalid),
                additionalTokenInformation
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String loginOrEmail, String tenant, String msgInvalidLogin)
            throws UsernameNotFoundException {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        List<FlyUserPermission> permissions = userService.getPermissions(loginOrEmail, tenant);

        if (permissions == null || permissions.isEmpty()) {
            throw new UsernameNotFoundException(msgInvalidLogin);
        }

        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPermissionName().toUpperCase())));

        return authorities;
    }

    public static String getCurrentLogin() {
        return FlyTokenUserDetails.getCurrentUsername();
    }
}