package com.bd_scott.app_bd_scott.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bd_scott.app_bd_scott.model.User;
import com.bd_scott.app_bd_scott.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping("/list")
    public String usersList(@RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "value", required = false) String value,
                                @PageableDefault(page = 0, size = 10) Pageable pageable,
                                Model model) {
        Page<User> results;
        if (value == null || value.isEmpty()) {
            results = userService.findAllPage(pageable);
        } else {
            results = userService.findByCriteria(type, value, pageable);
        }
        model.addAttribute("usersPage", results);
        model.addAttribute("tipoSeleccionado", type);
        model.addAttribute("valorBuscado", value);
        return "dept/list-users";
    }
}
