package com.bd_scott.app_bd_scott.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bd_scott.app_bd_scott.model.Role;
import com.bd_scott.app_bd_scott.model.User;
import com.bd_scott.app_bd_scott.repository.RoleRepository;
import com.bd_scott.app_bd_scott.repository.UserRepository;

@Configuration
public class DataLoader {
@Bean
    public CommandLineRunner initData(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Crear Roles si no existen
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_ADMIN"));
                roleRepository.save(new Role("ROLE_USER"));
            }

            // 2. Crear Usuario Admin si no existe
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setName("admin");
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                // ¡AQUÍ ESTÁ LA CLAVE! Encriptamos la contraseña antes de guardar
                admin.setPassword(passwordEncoder.encode("admin1234"));
                admin.setEnabled(true);

                // Asignar rol
                Role rolAdmin = roleRepository.findByNombre("ROLE_ADMIN").get();
                admin.addRole(rolAdmin);

                userRepository.save(admin);
                System.out.println("usuario ADMIN creado con pass: admin123");
            }
        };
    }
}
