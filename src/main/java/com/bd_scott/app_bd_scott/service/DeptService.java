package com.bd_scott.app_bd_scott.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bd_scott.app_bd_scott.model.Dept;

public interface DeptService {
    List<Dept> findAll();
    Page<Dept> findAllPage(Pageable pageable);
    List<Dept> findDistinctBy();
    void saveDept(Dept dept);
    Dept saveNewDept(Dept newDept);
    Optional<Dept> findById(Integer id);
    void deleteById(Integer id);
}
