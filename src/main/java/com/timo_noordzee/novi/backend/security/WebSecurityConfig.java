package com.timo_noordzee.novi.backend.security;

import com.timo_noordzee.novi.backend.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.timo_noordzee.novi.backend.domain.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthUserService authUserService;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().and()
                .authorizeRequests()
                .antMatchers("/employees/**").hasRole(ROLE_ADMIN)
                .antMatchers("/customers/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.GET, "/vehicles/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE, ROLE_MECHANIC)
                .antMatchers(HttpMethod.POST, "/vehicles/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.PUT, "/vehicles/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.DELETE, "/vehicles/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers("/shortcomings/**").hasAnyRole(ROLE_ADMIN, ROLE_MECHANIC)
                .antMatchers(HttpMethod.GET, "/vehiclePapers/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE, ROLE_MECHANIC)
                .antMatchers(HttpMethod.POST, "/vehiclePapers/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.PUT, "/vehiclePapers/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.DELETE, "/vehiclePapers/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMINISTRATIVE)
                .antMatchers("/parts/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.GET, "/parts/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE, ROLE_MECHANIC)
                .antMatchers(HttpMethod.POST, "/parts/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.PUT, "/parts/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.DELETE, "/parts/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.GET, "/actions/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE, ROLE_MECHANIC)
                .antMatchers(HttpMethod.POST, "/actions/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.PUT, "/actions/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.DELETE, "/actions/**").hasAnyRole(ROLE_ADMIN, ROLE_BACKOFFICE)
                .antMatchers(HttpMethod.GET, "/repairs/**").hasAnyRole(ROLE_ADMIN, ROLE_MECHANIC, ROLE_ADMINISTRATIVE, ROLE_CASHIER)
                .antMatchers(HttpMethod.POST, "/repairs/**").hasAnyRole(ROLE_ADMIN, ROLE_MECHANIC, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.PUT, "/repairs/**").hasAnyRole(ROLE_ADMIN, ROLE_MECHANIC, ROLE_ADMINISTRATIVE)
                .antMatchers(HttpMethod.DELETE, "/repairs/**").hasAnyRole(ROLE_ADMIN, ROLE_MECHANIC, ROLE_ADMINISTRATIVE)
                .antMatchers("/invoices/**").hasAnyRole(ROLE_ADMIN, ROLE_CASHIER);

        super.configure(http);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder());
        provider.setUserDetailsService(authUserService);
        return provider;
    }

}
