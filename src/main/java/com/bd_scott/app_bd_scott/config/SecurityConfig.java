package com.bd_scott.app_bd_scott.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
            authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/assets/**")
                    .permitAll()
                .requestMatchers("/")
                    .permitAll()
                .requestMatchers("/emp/**", "/dept/**")
                    .hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
            )
            .formLogin(login -> login
                .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                    .permitAll()
            )
            .exceptionHandling(hand -> hand
                .accessDeniedPage("/accessDenied")
            );
        return http.build();
    }
}
