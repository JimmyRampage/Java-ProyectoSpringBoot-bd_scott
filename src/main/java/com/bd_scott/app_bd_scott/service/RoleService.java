package com.bd_scott.app_bd_scott.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bd_scott.app_bd_scott.model.Role;

public interface RoleService {
    List<Role> findAll();
    Page<Role> findAllPage(Pageable pageable);
    Role saveRole(Role role);
    void deleteById(Integer id);
    Optional<Role> findById(Integer id);
}
