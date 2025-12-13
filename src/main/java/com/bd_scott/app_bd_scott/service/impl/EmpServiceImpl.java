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
        empRepository.deleteById(id);
    }

    @Override
    public List<Emp> findAll() {
        return empRepository.findAll();
    }

    @Override
    public Page<Emp> findAllPage(Pageable pageable) {
        return empRepository.findAll(pageable);
    }

    @Override
    public Optional<Emp> findById(Integer id) {
        return empRepository.findById(id);
    }

    @Override
    public void saveEmp(Emp emp) {
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
        switch (type) {
            case "ename":
                return empRepository.findByEnameIgnoreCaseContaining(value, pageable);
            case "job":
                return empRepository.findByJobIgnoreCaseContaining(value, pageable);
            case "sal":
                try {
                    return empRepository.findBySalGreaterThanEqual(Float.parseFloat(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            case "comm":
                try {
                    return empRepository.findByCommGreaterThanEqual(Float.parseFloat(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            case "deptno":
                try {
                    return empRepository.findByDept_Deptno(Integer.parseInt(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            default:
                break;
        }
        return empRepository.findAll(pageable);
    }

}
