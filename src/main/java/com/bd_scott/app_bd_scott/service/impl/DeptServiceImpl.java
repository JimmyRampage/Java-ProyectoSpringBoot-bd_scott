package com.bd_scott.app_bd_scott.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.repository.DeptRepository;
import com.bd_scott.app_bd_scott.repository.EmpRepository;
import com.bd_scott.app_bd_scott.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService{

    private final EmpRepository empRepository;

    private final DeptRepository deptRepository;

    public DeptServiceImpl(DeptRepository deptRepository, EmpRepository empRepository) {
        this.deptRepository = deptRepository;
        this.empRepository = empRepository;
    }

    @Override
    public List<Dept> findAll() {
        return deptRepository.findAll();
    }

    @Override
    public Page<Dept> findAllPage(Pageable pageable) {
        return deptRepository.findAll(pageable);
    }

    @Override
    public void saveDept(Dept dept) {
        deptRepository.save(dept);
    }

    @Override
    public Optional<Dept> findById(Integer id) {
        return deptRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
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
        switch (type) {
            case "deptno":
                return deptRepository.findDeptByDeptno(Integer.parseInt(value), pageable);
            case "dname":
                return deptRepository.findByDnameIgnoreCaseContaining(value, pageable);
            case "loc":
                return deptRepository.findByLocIgnoreCaseContaining(value, pageable);
            default:
                break;
        }
        return deptRepository.findAll(pageable);

    }

}
