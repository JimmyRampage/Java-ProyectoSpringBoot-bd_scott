package com.bd_scott.app_bd_scott.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.repository.DeptRepository;

import com.bd_scott.app_bd_scott.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService{

    private final DeptRepository deptRepository;

    public DeptServiceImpl(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    @Override
    public List<Dept> findAll() {
        return deptRepository.findAll();
    }

    @Override
    public Page<Dept> findAllPage(Pageable pageable) {
        if (pageable == null) return Page.empty();
        return deptRepository.findAll(pageable);
    }

    @Override
    public void saveDept(Dept dept) {
        if (dept == null) throw new IllegalArgumentException("Departamento no puede ser null");
        deptRepository.save(dept);
    }

    @Override
    public Optional<Dept> findById(Integer id) {
        if (id == null) return Optional.empty();
        return deptRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) throw new IllegalArgumentException("Id no puede ser null");
        deptRepository.deleteById(id);
    }

    @Override
    public Dept saveNewDept(Dept newDept) {
        Integer maxDeptno = deptRepository.findMaxDeptno();
        int nextDeptno = 10;
        if (maxDeptno != null) nextDeptno = maxDeptno + 10;
        newDept.setDeptno(nextDeptno);
        return deptRepository.save(newDept);
    }

    @Override
    public List<Dept> findDistinctBy() {
        return deptRepository.findDistinctBy();
    }

    @Override
    public Page<Dept> findByCriteria(String type, String value, Pageable pageable) {
        if (pageable == null) pageable = Pageable.unpaged();
        if (value == null || type == null) return Page.empty();
        try {
            return switch (type.toLowerCase()) {
                case "deptno" -> deptRepository.findDeptByDeptno(Integer.parseInt(value), pageable);
                case "dname" -> deptRepository.findByDnameIgnoreCaseContaining(value, pageable);
                case "loc" -> deptRepository.findByLocIgnoreCaseContaining(value, pageable);
                default -> deptRepository.findAll(pageable);
            };
        } catch (Exception e) {
            return Page.empty();
        }
    }

}
