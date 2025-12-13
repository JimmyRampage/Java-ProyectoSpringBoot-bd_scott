package com.bd_scott.app_bd_scott.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bd_scott.app_bd_scott.model.Dept;
import com.bd_scott.app_bd_scott.model.Emp;

public interface EmpRepository extends JpaRepository<Emp, Integer>{
    List<Emp> findByDept(Dept dept);

    /**
     * Saber si un id existe o no.
     * Lo utilizare para que el usuario ingrese un id en el formulario y este le dira si puede o no usarlo.
     * @param empNo
     * @return true si existe, false si no existe
     */
    boolean existsByEmpno(Integer empNo);

    // Para buscador
    Page<Emp> findByJobIgnoreCaseContaining(String job, Pageable pageable);
    Page<Emp> findByEnameIgnoreCaseContaining(String ename, Pageable pageable);
    Page<Emp> findBySalGreaterThanEqual(Float sal, Pageable pageable);
    Page<Emp> findByCommGreaterThanEqual(Float comm, Pageable pageable);
    Page<Emp> findByDept_Deptno(Integer deptno, Pageable pageable);
}
