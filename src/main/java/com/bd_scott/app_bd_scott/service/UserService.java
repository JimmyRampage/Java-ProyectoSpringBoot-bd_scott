package com.bd_scott.app_bd_scott.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.bd_scott.app_bd_scott.dto.UserRegistrationDto;
import com.bd_scott.app_bd_scott.model.Role;
import com.bd_scott.app_bd_scott.model.User;

public interface UserService {
    List<User> findAll();
    Page<User> findAllPage(Pageable pageable);
    User save(UserRegistrationDto userRegistrationDto);
    Optional<User> findById(Integer id);
    // List<User> findByRole(Role role);
    void deleteById(Integer id);
    Page<User> findByCriteria(String type, String value, Pageable pageable);
}