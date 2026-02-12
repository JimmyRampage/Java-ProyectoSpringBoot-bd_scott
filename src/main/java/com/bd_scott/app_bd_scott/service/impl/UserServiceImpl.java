package com.bd_scott.app_bd_scott.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bd_scott.app_bd_scott.dto.UserRegistrationDto;
import com.bd_scott.app_bd_scott.model.User;
import com.bd_scott.app_bd_scott.repository.RoleRepository;
import com.bd_scott.app_bd_scott.repository.UserRepository;
import com.bd_scott.app_bd_scott.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setName(userRegistrationDto.getName());
        user.setUsername(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setEnabled(true);
        user.addRole(
            roleRepository
                .findByNombre("ROLE_USER")
                .orElseThrow(
                    () -> new RuntimeException("Error: Rol USER no encontrado."))
        );
        return userRepository.save(user);
    }
}
