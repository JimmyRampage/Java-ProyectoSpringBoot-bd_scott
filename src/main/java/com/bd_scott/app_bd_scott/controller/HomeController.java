package com.bd_scott.app_bd_scott.controller;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String showHome() {
        return "home";
    }

    @GetMapping("/accessDenied")
    public String showAccessDenied() {
        return "error/accessDenied"; // Retorna la vista accessDenied.html
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        return "redirect:/";
    }

}
