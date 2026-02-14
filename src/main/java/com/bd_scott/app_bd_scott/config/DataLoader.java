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

    private final UserRepository userRepository;

    DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
@Bean
    public CommandLineRunner initData(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear Roles si no existen
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_ADMIN"));
                roleRepository.save(new Role("ROLE_MODERATOR"));
                roleRepository.save(new Role("ROLE_USER"));
            }

            //
            // CREA usuarios: admin, moderator, user si no existen
            //
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setName("admin");
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin1234"));
                admin.setEnabled(true);

                // Asignar rol
                Role rolAdmin = roleRepository.findByNombre("ROLE_ADMIN").get();
                admin.addRole(rolAdmin);

                userRepository.save(admin);
                System.out.println("usuario ADMIN creado con pass: admin1234");
            }
            if (userRepository.findByUsername("moderator").isEmpty()) {
                User moderator = new User();
                moderator.setName("moderator");
                moderator.setUsername("moderator");
                moderator.setEmail("moderator@moderator.com");
                moderator.setPassword(passwordEncoder.encode("moderator1234"));
                moderator.setEnabled(true);

                // Asignar rol
                Role rolModerator = roleRepository.findByNombre("ROLE_MODERATOR").get();
                moderator.addRole(rolModerator);

                userRepository.save(moderator);
                System.out.println("usuario MODERATOR creado con pass: moderator1234");
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setName("user");
                user.setUsername("user");
                user.setEmail("user@user.com");
                user.setPassword(passwordEncoder.encode("user1234"));
                user.setEnabled(true);

                // Asignar rol
                Role rolUser = roleRepository.findByNombre("ROLE_USER").get();
                user.addRole(rolUser);

                userRepository.save(user);
                System.out.println("usuario USER creado con pass: user1234");
            }
        };
    }
}
