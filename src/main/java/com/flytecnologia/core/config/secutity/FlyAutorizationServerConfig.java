package com.flytecnologia.core.config.secutity;

import com.flytecnologia.core.token.FlyJwtTokenStore;
import com.flytecnologia.core.token.FlyTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
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


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("angular")
                .secret(new BCryptPasswordEncoder().encode(secretKeyAngular))
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(60 * 60) //duration's token
                .refreshTokenValiditySeconds(3600 * 24) //1 day
            .and()
                .withClient("mobile")
                .secret(new BCryptPasswordEncoder().encode(secretKeyMobile))
                .scopes("read", "mobile")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(60 * 60 * 24 * 365) //duration's token 365 dias
                .refreshTokenValiditySeconds(60 * 60 * 24 * 365) //duration's token 365 dias
        ;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter));

        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .reuseRefreshTokens(false);

       /* endpoints
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter)
                .reuseRefreshTokens(false)
                .authenticationManager(authenticationManager);*/
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(secretKey);
        //converter.setVerifierKey(secretKey);

        return converter;
    }

    /*local de armazenagem dos tokens*/
    @Bean
    public TokenStore tokenStore() {
        return new FlyJwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new FlyTokenEnhancer();
    }
}
