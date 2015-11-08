package com.test.smvc.model;

import java.util.List;

import com.smvc.dao.annotation.GeneratedKeys;
import com.smvc.dao.annotation.Table;

@Table(name = "student")
public class Student {
    
    @GeneratedKeys
    private Long id;
    private String name;
    private String password;
    private String school;
    
    //private List<Grade> grades;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

//    public List<Grade> getGrades() {
//        return grades;
//    }
//
//    public void setGrades(List<Grade> grades) {
//        this.grades = grades;
//    }

    public Student(String name, String password, String school) {

        this.name = name;
        this.password = password;
        this.school = school;
    }

    public Student() {

    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", password=" + password + ", school=" + school + "]";
    }

}
