package com.flytecnologia.core.config.secutity;

import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.security.FlyAccessDeniedHandler;
import com.flytecnologia.core.security.FlyMethodSecurityExpressionRoot;
import com.flytecnologia.core.user.FlyUserDetailsService;
import com.flytecnologia.core.user.FlyUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.AccessDeniedHandler;

@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class FlyResourceServerConfig extends ResourceServerConfigurerAdapter {
    private static final String[] AUTH_WHITELIST_OTHERS = {
            "/actuator/health"
    };

    private static final String[] AUTH_WHITELIST_SWAGGER = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
    };

    private static final String[] AUTH_WHITELIST_LOGIN = {
            "/login",
            "/login/reset-password",
            "/login/send-new-password"
    };

    private FlyAppProperty flyAppProperty;
    private UserDetailsService userDetailsService;
    private FlyUserService userService;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * /Login qualquer um acessa, no mais, qualquer requeste deve estar autenticado.
     * <p>
     * Configua a api rest para não manter estado de nada. Stateless
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST_OTHERS).permitAll()
                .antMatchers(AUTH_WHITELIST_LOGIN).permitAll()
                .antMatchers(AUTH_WHITELIST_SWAGGER).permitAll()
                .anyRequest().authenticated()
            .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        ;
    }

    /**
     * Garante que o servidor vai ser stateless
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.stateless(true);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new FlyUserDetailsService(userService);
    }

    @Bean
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new FlyMethodSecurityExpressionRoot(flyAppProperty);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new FlyAccessDeniedHandler(flyAppProperty);
    }
}
