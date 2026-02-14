package com.bd_scott.app_bd_scott.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bd_scott.app_bd_scott.model.User;
import com.bd_scott.app_bd_scott.service.RoleService;
import com.bd_scott.app_bd_scott.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final RoleService roleService;


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
        return "users/list-users";
    }

    @GetMapping("/createUsers")
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("editMode", "false");
        return "users/form-users";
    }

    @PostMapping("/saveUser")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "users/form-users";
        }
        try {
            userService.saveUser(user);
            return "redirect:/users/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("id", "error.user");
            model.addAttribute("error", e.getMessage());
            return "users/form-users";
        }
    }

    @GetMapping("/edit/{id}")
    public String updateUser(
            @PathVariable("id") Integer idUser,
            Model model) {
        model.addAttribute("user", userService.findById(idUser).get());
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("editMode", "true");
        return "users/form-users";
    }

    @PostMapping("/update")
    public String updateUser(
            @Valid
            @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editModel", "true");
            model.addAttribute("allRoles", roleService.findAll());
            return "users/form-users";
        }
        userService.saveUser(user);
        return "redirect:/users/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable("id") Integer idUser,
            RedirectAttributes attributes) {
        userService.deleteById(idUser);
        attributes.addFlashAttribute("msg", "User deleted");
        return "redirect:/users/list";
    }
}
