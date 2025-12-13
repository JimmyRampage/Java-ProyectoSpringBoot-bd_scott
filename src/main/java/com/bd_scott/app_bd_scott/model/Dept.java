package com.bd_scott.app_bd_scott.model;

import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Dept {

    @Id
    @Column(name = "deptno", nullable = false)
    //@NotNull(message = "El 'DEPTNO' no puede ser nulo")
    //@Min(value = 1, message = "El 'DEPTNO' debe ser mayor que 0")
    private Integer deptno;

    @Column(name = "dname", length = 45, nullable = false)
    @NotEmpty(message = "El 'DNAME' no puede estar vacío")
    @Size(min = 2, max = 45, message = "El 'DNAME' debe tener como mínimo 2 y como máximo 45 caracteres")
    private String dname;

    @Column(name = "loc", length = 45)
    @Size(min = 2, max = 45, message = "El 'LOC' debe tener como mínimo 2 y como máximo 45 caracteres")
    private String loc;

    @OneToMany(mappedBy = "dept")
    private Set<Emp> employees;

    public Dept() {
    }

    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Set<Emp> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Emp> employees) {
        this.employees = employees;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deptno == null) ? 0 : deptno.hashCode());
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
        Dept other = (Dept) obj;
        if (deptno == null) {
            if (other.deptno != null)
                return false;
        } else if (!deptno.equals(other.deptno))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Dept [deptno=" + deptno + ", dname=" + dname + ", loc=" + loc + "]";
    }



}
