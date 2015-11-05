package com.test.smvc.model;

import com.smvc.dao.annotation.Table;
/**
 * 
 * @author Big Martin
 *
 */
@Table(name = "grade")
public class Grade {
    private int id;
    
    /**
     * 课程名称
     */
    private String className;
    
    /**
     * 成绩
     */
    private int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    
}
