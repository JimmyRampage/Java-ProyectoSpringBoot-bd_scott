package com.bd_scott.app_bd_scott.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                                () -> new RuntimeException("Error: Rol USER no encontrado.")));
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Page<User> findAllPage(Pageable pageable) {
        if (pageable == null)
            return Page.empty();
        return this.userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Integer id) {
        if (id == null)
            return Optional.empty();
        return this.userRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id no puede ser null");
        }
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado para eliminar");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Page<User> findByCriteria(String type, String value, Pageable pageable) {
        if (pageable == null)
            pageable = Pageable.unpaged();
        if (value == null || type == null)
            return Page.empty();

        return switch (type.toLowerCase()) {
            case "name" -> userRepository.findByNameIgnoreCaseContaining(value, pageable);
            case "role" -> userRepository.findByRoles(value, pageable);
            default -> userRepository.findAll(pageable);
        };
    }

    @Override
    public User saveUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("Usuario no puede ser null");

        if (user.getId() != null) {
            User userOld = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado con ID " + user.getId()));

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(userOld.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(user);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
