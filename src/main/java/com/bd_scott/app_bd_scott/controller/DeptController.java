package com.bd_scott.app_bd_scott.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.service.DeptService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/dept")
public class DeptController {
    // Inyeccion del DeptService
    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @GetMapping("/list")
    public String getMethodName(@RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "value", required = false) String value,
                                @PageableDefault(page = 0, size = 10) Pageable pageable,
                                Model model) {
        Page<Dept> results;
        if (value == null || value.isEmpty()) {
            results = deptService.findAllPage(pageable);
        } else {
            results = deptService.findByCriteria(type, value, pageable);
        }
        model.addAttribute("deptPage", results);
        model.addAttribute("tipoSeleccionado", type);
        model.addAttribute("valorBuscado", value);
        return "dept/list-dept";
    }

    @GetMapping("/createDept")
    public String createDept(Model model) {
        model.addAttribute("dept", new Dept());
        model.addAttribute("editMode", "false");
        return "dept/form-dept";
    }

    @PostMapping("/saveDept")
    public String saveDept(
            @Valid @ModelAttribute("dept") Dept dept,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "dept/form-dept";
        }
        try {
            deptService.saveNewDept(dept);
            return "redirect:/dept/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("deptno", "error.dept", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "dept/form-dept";
        }
    }

    @GetMapping("/edit/{id}")
    public String updateDept(
            @PathVariable("id") Integer idDept,
            Model model) {
        model.addAttribute("dept", deptService.findById(idDept).get());
        model.addAttribute("editMode", "true");
        return "dept/form-dept";
    }

    @PostMapping("/update")
    public String updateDept(
            @Valid @ModelAttribute("dept") Dept dept,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editModel", "true");
            return "dept/form-dept";
        }
        deptService.saveDept(dept);
        return "redirect:/dept/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteDept(
            @PathVariable("id") Integer idDept,
            RedirectAttributes attributes) {
        deptService.deleteById(idDept);
        attributes.addFlashAttribute("msg", "Department deleted");
        return "redirect:/dept/list";
    }
}
