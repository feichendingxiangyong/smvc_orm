package com.test.smvc;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.smvc.dao.DbPlugin;
import com.smvc.dao.annotation.Parameter;
import com.test.smvc.model.Grade;
import com.test.smvc.model.Student;

/**
 * TEST DB PLUGIN
 * @author Big Martin
 *
 */
public class testDbPlugin {

    @Test
    public void addOneObj() throws IllegalArgumentException, IllegalAccessException
    {
        DbPlugin<Student> studentDao = new DbPlugin<Student>();
        DbPlugin<Grade> gradeDao = new DbPlugin<Grade>();
        Student stu=new Student("martin", "121", "TaiWan");
        
        //query all users named martin
        Parameter param = new Parameter("name", "martin");
        
        //clear all users named martin
        studentDao.delete(Student.class, param);
        
        //clear all grades
        gradeDao.deleteAll(Grade.class);

        List<Student> students = studentDao.queryList(Student.class, param);
        List<Grade> grades = gradeDao.queryAllList(Grade.class);
        Assert.assertTrue(students.isEmpty());
        Assert.assertTrue(grades.isEmpty());
        
        //insert
        //int affectedRows = dao.save(stu);
        long studentId = studentDao.saveWithGeneratedKeys(stu);
        
        List<Grade> gradesList = new ArrayList<Grade>();
        gradesList.add(new Grade(1, (int)studentId, "Math", 94));
        gradesList.add(new Grade(2, (int)studentId, "English", 98));
        gradeDao.saveList(gradesList);
        
        List<Student> students2 = studentDao.queryList(Student.class, param);
        List<Grade> grades2 = gradeDao.queryAllList(Grade.class);
        Assert.assertTrue(students2.size() == 1);
        Assert.assertTrue(grades2.size() == 2);
        
        //update
        stu.setSchool("Nanjin");
        
        studentDao.update(stu, param);
        
        //query
        Student student = studentDao.query(Student.class, param);
        Assert.assertEquals("Nanjin", student.getSchool()); 
        
    }
    
}
