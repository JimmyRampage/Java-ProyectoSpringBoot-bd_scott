package com.bd_scott.app_bd_scott.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Dept {

    @Id
    @Column(name = "deptno", nullable = false)
    private Integer deptno;

    @Column(name = "dname", length = 45, nullable = false)
    private String dname;

    @Column(name = "loc", length = 45)
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
