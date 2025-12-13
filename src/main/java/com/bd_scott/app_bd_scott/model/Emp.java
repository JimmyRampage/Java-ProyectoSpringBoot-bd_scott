package com.bd_scott.app_bd_scott.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "emp")
@NamedQueries({
    @NamedQuery(name = "emp.findAll", query = "SELECT e FROM Emp e")
})
public class Emp {

    @Id
    @Column(name = "empno", nullable = false)
    @NotNull(message = "El 'EMPNO' no puede ser nulo")
    @Min(value = 1, message = "El 'EMPNO' debe ser mayor que 0")
    private Integer empno;

    @Column(name = "ename", length = 45)
    @NotEmpty(message = "El 'ENAME' no puede estar vacío")
    @Size(min = 2, max = 45, message = "El 'ENAME' debe tener como mínimo 2 y como máximo 45 caracteres")
    private String ename;

    @Column(name = "job", length = 9)
    @Size(min = 2, max = 9, message = "El 'JOB' debe tener como mínimo 2 y como máximo 9 caracteres")
    private String job;

    @ManyToOne
    @JoinColumn(name = "mgr", referencedColumnName = "empno")
    private Emp manager;

    @Column(name = "sal") // definir que tipo de float
    @Min(value = 1, message = "El 'SAL' debe ser mayor que 0")
    private Float sal;

    @Column(name = "comm")
    @Min(value = 1, message = "El 'COMM' debe ser mayor que 0")
    private Float comm;

    @ManyToOne
    @JoinColumn(name = "deptno", referencedColumnName = "deptno")
    @NotNull(message = "El 'DEPTNO' no puede ser nulo")
    private Dept dept;

    @Column(name = "hiredate")
    @NotNull(message = "La 'FECHA DE CONTRATACIÓN' no puede ser nula")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hiredate;

    public Emp() {
    }

    public Integer getEmpno() {
        return empno;
    }

    public void setEmpno(Integer empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Emp getManager() {
        return manager;
    }

    public void setManager(Emp manager) {
        this.manager = manager;
    }

    public Float getSal() {
        return sal;
    }

    public void setSal(Float sal) {
        this.sal = sal;
    }

    public Float getComm() {
        return comm;
    }

    public void setComm(Float comm) {
        this.comm = comm;
    }

    public Dept getDeptno() {
        return dept;
    }

    public void setDeptno(Dept dept) {
        this.dept = dept;
    }

    public LocalDate getHiredate() {
        return hiredate;
    }

    public void setHiredate(LocalDate hiredate) {
        this.hiredate = hiredate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((empno == null) ? 0 : empno.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Emp other = (Emp) obj;
        if (empno == null) {
            if (other.empno != null)
                return false;
        } else if (!empno.equals(other.empno))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Emp [empno=" + empno);
        sb.append(", ename=" + ename);
        sb.append(", job=" + job);
        sb.append(", manager=" + (manager == null ? "null" : manager.getEmpno().toString()));
        sb.append(", sal=" + sal);
        sb.append(", comm=" + comm);
        sb.append(", dept=" + dept);
        sb.append(", hiredate=" + hiredate);
        return sb.toString();
    }
}
