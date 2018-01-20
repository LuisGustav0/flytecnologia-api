package com.flytecnologia.core.token;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.user.FlyUserDetails;
import com.flytecnologia.core.user.FlyUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class FlyTokenEnhancer implements TokenEnhancer{
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        FlyUser user = ((FlyUserDetails) authentication.getUserAuthentication().getPrincipal()).getUser();

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(FlyMultiTenantConstants.REQUEST_HEADER_ID,
                user.getTenant().replace(FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX,""));
        additionalInfo.put("username", user.getUsername());
        additionalInfo.put("userId", user.getId());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}
