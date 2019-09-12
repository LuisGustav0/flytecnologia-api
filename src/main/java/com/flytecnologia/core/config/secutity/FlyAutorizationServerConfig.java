package com.flytecnologia.core.config.secutity;

import com.flytecnologia.core.token.FlyJwtTokenStore;
import com.flytecnologia.core.token.FlyTokenEnhancer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class FlyAutorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String secretKey;

    @Value("${security.oauth2.resource.jwt.key-value-angular}")
    private String secretKeyAngular;

    @Value("${security.oauth2.resource.jwt.key-value-mobile}")
    private String secretKeyMobile;

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;

    private static final String GRANT_TYPES_PASSWORD = "password";
    private static final String GRANT_TYPES_REFRESH_TOKEN = "refresh_token";
    private static final String CLIENT_ANGULAR = "angular";
    private static final String CLIENT_MOBILE = "mobile";

    private static final String SCOPE_READ = "read";
    private static final String SCOPE_WRITE = "write";
    private static final String SCOPE_MOBILE = "mobile";

    public FlyAutorizationServerConfig(UserDetailsService userDetailsService,
                                       AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(CLIENT_ANGULAR)
                .secret(new BCryptPasswordEncoder().encode(secretKeyAngular))
                .scopes(SCOPE_READ, SCOPE_WRITE)
                .authorizedGrantTypes(GRANT_TYPES_PASSWORD, GRANT_TYPES_REFRESH_TOKEN)
                .accessTokenValiditySeconds(60 * 60 * 2) //duration's token -> ao renovar o token, considerar o tenant do token atual....ao renover volta pro tenant do usuario
                .refreshTokenValiditySeconds(3600 * 24) //1 day
                .and()
                .withClient(CLIENT_MOBILE)
                .secret(new BCryptPasswordEncoder().encode(secretKeyMobile))
                .scopes(SCOPE_READ, SCOPE_MOBILE)
                .authorizedGrantTypes(GRANT_TYPES_PASSWORD, GRANT_TYPES_REFRESH_TOKEN)
                .accessTokenValiditySeconds(60 * 60 * 24 * 365) //duration's token 365 dias
                .refreshTokenValiditySeconds(60 * 60 * 24 * 365) //duration's token 365 dias
        ;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        JwtAccessTokenConverter jwtAccessTokenConverter = jwtAccessTokenConverter();

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));

        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(secretKey);

        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new FlyJwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new FlyTokenEnhancer();
    }
}
