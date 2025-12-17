package com.bd_scott.app_bd_scott.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.model.Emp;
import com.bd_scott.app_bd_scott.repository.EmpRepository;
import com.bd_scott.app_bd_scott.service.EmpService;

@Service
public class EmpServiceImpl implements EmpService{

    private final EmpRepository empRepository;

    public EmpServiceImpl(EmpRepository empRepository){
        this.empRepository = empRepository;
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id no puede ser null");
        }
        if (!empRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado para eliminar");
        }
        empRepository.deleteById(id);
    }

    @Override
    public List<Emp> findAll() {
        return empRepository.findAll();
    }

    @Override
    public Page<Emp> findAllPage(Pageable pageable) {
        if (pageable == null) return Page.empty();
        return empRepository.findAll(pageable);
    }

    @Override
    public Optional<Emp> findById(Integer id) {
        if (id == null) return Optional.empty();
        return empRepository.findById(id);
    }

    @Override
    public void saveEmp(Emp emp) {
        if (emp == null) throw new IllegalArgumentException("Empleado no puede ser null");
        empRepository.save(emp);
    }

    @Override
    public Emp saveNewEmp(Emp emp) {
        Integer empnoFromUser = emp.getEmpno();
        if(empnoFromUser == null || empnoFromUser <= 0) {
            throw new IllegalArgumentException("El 'EMPNO' no puede ser nullo o <= 0");
        }
        if (empRepository.existsByEmpno(empnoFromUser)) {
            throw new IllegalArgumentException("El 'EMPNO' ya existe");
        }
        return empRepository.save(emp);
    }

    @Override
    public List<Emp> findByDept(Dept dept) {
        return empRepository.findByDept(dept);
    }

    @Override
    public Page<Emp> findByCriteria(String type, String value, Pageable pageable) {
        if (pageable == null) pageable = Pageable.unpaged();
        if (value == null || type == null) return Page.empty();
        try {
            return switch (type.toLowerCase()) {
                case "ename" -> empRepository.findByEnameIgnoreCaseContaining(value, pageable);
                case "job" -> empRepository.findByJobIgnoreCaseContaining(value, pageable);
                case "sal" -> empRepository.findBySalGreaterThanEqual(Float.parseFloat(value), pageable);
                case "comm" -> empRepository.findByCommGreaterThanEqual(Float.parseFloat(value), pageable);
                case "deptno" -> empRepository.findByDept_Deptno(Integer.parseInt(value), pageable);
                default -> empRepository.findAll(pageable);
            };
        } catch (NumberFormatException e) {
            return Page.empty();
        }
    }
}
