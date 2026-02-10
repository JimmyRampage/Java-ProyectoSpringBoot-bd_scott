package com.bd_scott.app_bd_scott.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    public UserDetails loadUserByUsername(String username);
}
