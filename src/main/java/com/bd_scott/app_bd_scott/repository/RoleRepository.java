package com.bd_scott.app_bd_scott.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bd_scott.app_bd_scott.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);
}