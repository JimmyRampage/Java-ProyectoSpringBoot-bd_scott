package com.bd_scott.app_bd_scott.repository;

import com.bd_scott.app_bd_scott.model.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByUsername(String username);
    Page<User> findByRoles(String role, Pageable pageable);
    Page<User> findByNameIgnoreCaseContaining(String name, Pageable pageable);
}
