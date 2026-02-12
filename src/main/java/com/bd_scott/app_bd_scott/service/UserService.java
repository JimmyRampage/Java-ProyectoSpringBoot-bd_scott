package com.bd_scott.app_bd_scott.service;

import com.bd_scott.app_bd_scott.dto.UserRegistrationDto;
import com.bd_scott.app_bd_scott.model.User;

public interface UserService {
    public User save(UserRegistrationDto userRegistrationDto);
}
