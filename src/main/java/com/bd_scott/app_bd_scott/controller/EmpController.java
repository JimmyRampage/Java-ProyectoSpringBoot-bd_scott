package com.bd_scott.app_bd_scott.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bd_scott.app_bd_scott.model.Emp;
import com.bd_scott.app_bd_scott.service.DeptService;
import com.bd_scott.app_bd_scott.service.EmpService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/emp")
public class EmpController {
    // Inyeccion del EmpService
    private final EmpService empService;
    private final DeptService deptService;


    public EmpController(EmpService empService, DeptService deptService) {
        this.empService = empService;
        this.deptService = deptService;
    }

    @GetMapping("/list")
    public String listEmployees(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "value", required = false) String value,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        Model model) {
        Page<Emp> results;
        if (value == null || value.isEmpty()) {
            results = empService.findAllPage(pageable);
        } else {
            results = empService.findByCriteria(type, value, pageable);
        }
        model.addAttribute("empPage", results);
        model.addAttribute("tipoSeleccionado", type);
        model.addAttribute("valorBuscado", value);
        return "emp/list-emp";
    }

    @GetMapping("/createEmp")
    public String createEmployee(Model model) {
        model.addAttribute("emp", new Emp());
        model.addAttribute("emps", empService.findAll());
        model.addAttribute("depts", deptService.findDistinctBy());
        model.addAttribute("editMode", "false");
        return "emp/form-emp";
    }
    

    @PostMapping("/saveEmp")
    public String saveEmployee(@Valid
            @ModelAttribute("emp") Emp emp,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("emps", empService.findAll());
            model.addAttribute("depts", deptService.findDistinctBy());
            return "emp/form-emp";
        }
        try {
            empService.saveNewEmp(emp);
            return "redirect:/emp/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("empno", "error.emp", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "emp/form-emp";
        }
    }

    @GetMapping("/edit/{id}")
    public String updateEmp(@PathVariable("id") Integer idEmp, Model model){
        model.addAttribute("emp", empService.findById(idEmp).get());
        model.addAttribute("emps", empService.findAll());
        model.addAttribute("depts", deptService.findDistinctBy());
        model.addAttribute("editMode", "true");
        return "emp/form-emp";
    }

    @PostMapping("/update")
    public String updateEmployee(@Valid @ModelAttribute("emp") Emp emp, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", "true");
            model.addAttribute("emps", empService.findAll());
            model.addAttribute("depts", deptService.findDistinctBy());
            return "emp/form-emp";
        }
        empService.saveEmp(emp);
        return "redirect:/emp/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmp(@PathVariable("id") Integer idEmp, RedirectAttributes attributes) {
        empService.deleteById(idEmp);
        attributes.addFlashAttribute("msg", "Employee deleted");
        return "redirect:/emp/list";
    }
}
