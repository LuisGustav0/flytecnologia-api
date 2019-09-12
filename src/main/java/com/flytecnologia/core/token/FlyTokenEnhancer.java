package com.flytecnologia.core.token;

import com.flytecnologia.core.user.FlyUser;
import com.flytecnologia.core.user.FlyUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX;
import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.REQUEST_HEADER_ID;

public class FlyTokenEnhancer implements TokenEnhancer{
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final FlyUserDetails userDetails = ((FlyUserDetails) authentication.getUserAuthentication().getPrincipal());
        final FlyUser user = userDetails.getUser();

        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(REQUEST_HEADER_ID,
                user.getTenant().replace(DEFAULT_TENANT_SUFFIX,""));
        additionalInfo.put("username", user.getUsername());
        additionalInfo.put("userId", user.getId());

        if(userDetails.getAdditionalTokenInformation() != null){
            additionalInfo.putAll(userDetails.getAdditionalTokenInformation());
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}
