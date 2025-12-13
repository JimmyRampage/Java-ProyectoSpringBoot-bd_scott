package com.bd_scott.app_bd_scott.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bd_scott.app_bd_scott.model.Dept;

public interface DeptRepository extends JpaRepository<Dept, Integer>{

    @Query("SELECT MAX(d.deptno) FROM Dept d")
    Integer findMaxDeptno();

    List<Dept> findDistinctBy();

    Page<Dept> findDeptByDeptno(Integer deptno, Pageable pageable);
    Page<Dept> findByDnameIgnoreCaseContaining(String dname, Pageable pageable);
    Page<Dept> findByLocIgnoreCaseContaining(String loc, Pageable pageable);
}
