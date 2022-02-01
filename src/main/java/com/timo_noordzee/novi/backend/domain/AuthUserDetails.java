package com.timo_noordzee.novi.backend.domain;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorityList;

    public AuthUserDetails(final EmployeeEntity employeeEntity) {
        this.email = employeeEntity.getEmail();
        this.password = employeeEntity.getPassword();
        this.authorityList = new ArrayList<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority("ROLE_" + employeeEntity.getRole().name()));
        }};
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
