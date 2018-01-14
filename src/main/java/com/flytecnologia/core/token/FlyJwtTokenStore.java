package com.flytecnologia.core.token;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * https://github.com/spring-projects/spring-security-oauth/issues/183
 */
public class FlyJwtTokenStore extends JwtTokenStore {

    public FlyJwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
        super(jwtTokenEnhancer);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication oAuth2Authentication = super.readAuthentication(token);
        oAuth2Authentication.setDetails(token.getAdditionalInformation());
        return oAuth2Authentication;
    }
}