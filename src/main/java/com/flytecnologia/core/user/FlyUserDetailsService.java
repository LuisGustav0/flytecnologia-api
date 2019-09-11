package com.flytecnologia.core.user;

import com.flytecnologia.core.token.FlyTokenUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class FlyUserDetailsService implements UserDetailsService {
    private FlyUserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        final String loginInvalid = userService.getMessageInvalidLogin();

        final Optional<FlyUser> user = userService.findByLoginOrEmail(login);

        final FlyUser flyUser = user.orElseThrow(() -> new UsernameNotFoundException(loginInvalid));

        final Map<String, Object> additionalTokenInformation = userService.getAdditionalTokenInformation(flyUser, login, flyUser.getId());

        return new FlyUserDetails(flyUser,
                getAuthorities(login, flyUser.getTenant(), loginInvalid),
                additionalTokenInformation
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String loginOrEmail, String tenant, String msgInvalidLogin)
            throws UsernameNotFoundException {
        final Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        final List<FlyUserPermission> permissions = userService.getPermissions(loginOrEmail, tenant);

        if (permissions == null || permissions.isEmpty()) {
            throw new UsernameNotFoundException(msgInvalidLogin);
        }

        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPermissionName())));

        return authorities;
    }

    public static String getCurrentLogin() {
        return FlyTokenUserDetails.getCurrentUsername();
    }
}