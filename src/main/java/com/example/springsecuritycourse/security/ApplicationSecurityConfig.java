package com.example.springsecuritycourse.security;

import com.example.springsecuritycourse.auth.ApplicationUserService;
import com.example.springsecuritycourse.jwt.JwtConfig;
import com.example.springsecuritycourse.jwt.JwtTokenVerifier;
import com.example.springsecuritycourse.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static com.example.springsecuritycourse.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserService applicationUserService,
                                     JwtConfig jwtConfig,
                                     SecretKey secretKey) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}
