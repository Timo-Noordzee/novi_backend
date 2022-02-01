package com.timo_noordzee.novi.backend.service;

import com.timo_noordzee.novi.backend.data.EmployeeEntity;
import com.timo_noordzee.novi.backend.domain.AuthUserDetails;
import com.timo_noordzee.novi.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findByEmail(email);

        if (employeeEntityOptional.isPresent()) {
            final EmployeeEntity employeeEntity = employeeEntityOptional.get();
            return new AuthUserDetails(employeeEntity);
        } else {
            throw new UsernameNotFoundException(String.format("user with email %s doesn't exist", email));
        }
    }

}
