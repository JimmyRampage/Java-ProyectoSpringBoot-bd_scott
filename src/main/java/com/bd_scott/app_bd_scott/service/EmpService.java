package com.bd_scott.app_bd_scott.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.model.Emp;

public interface EmpService {
    List<Emp> findAll();
    Page<Emp> findAllPage(Pageable pageable);
    void saveEmp(Emp emp);
    Emp saveNewEmp(Emp newEmp);
    Optional<Emp> findById(Integer id);
    List<Emp> findByDept(Dept dept);
    void deleteById(Integer id);
    Page<Emp> findByCriteria(String type, String value, Pageable pageable);
}
