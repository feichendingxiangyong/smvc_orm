package com.test.smvc;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.smvc.dao.DbPlugin;
import com.smvc.dao.annotation.Parameter;
import com.test.smvc.model.Student;


public class testDbPlugin {

	
	@Test
	public void add()
	{
		DbPlugin<Student> dao = new DbPlugin<Student>();
		Student stu=new Student("martin", "121", "TaiWan");
		
	    //query all users named martin
        Parameter param = new Parameter("name", "martin");
        
		//clear all users named martin
		dao.delete(Student.class, param);

		List<Student> students = dao.queryList(Student.class, param);
		
		Assert.assertTrue(students.isEmpty());
		
		//insert
        dao.save(stu);
        
        List<Student> students2 = dao.queryList(Student.class, param);
        
        Assert.assertTrue(students2.size() == 1);
        
        System.out.println(students2.get(0));
        
        //update
        stu.setSchool("Nanjin");
        
        dao.update(stu, param);
        
        //query
        Student student = dao.query(Student.class, param);
        Assert.assertEquals("Nanjin", student.getSchool()); 
        
        //delete
        //clear all users named martin
        //dao.delete(Student.class, param);
	    
        //Student student2 = dao.query(Student.class, param);
        //Assert.assertNull(student2);
	}
}
