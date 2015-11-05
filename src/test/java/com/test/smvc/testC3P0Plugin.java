package com.test.smvc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class testC3P0Plugin
{
    private ComboPooledDataSource cpds = null;
    private String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/test";
    private String user = "root";
    private String password = "111111";
    private String driverClass = "com.mysql.jdbc.Driver";
    private int maxPoolSize = 1;
    private int minPoolSize = 1;
    private int initialPoolSize = 1;
    private int maxIdleTime = 10;
    private int acquireIncrement = 2;
    private int unreturnedConnectionTimeout = 10;

    public testC3P0Plugin()
    {
        cpds = new ComboPooledDataSource();
        try
        {
            cpds.setDriverClass(driverClass);
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }           
        cpds.setJdbcUrl(jdbcUrl);
        cpds.setUser(user);                                  
        cpds.setPassword(password);
        cpds.setMaxPoolSize(maxPoolSize);
        cpds.setMinPoolSize(minPoolSize);
        cpds.setInitialPoolSize(initialPoolSize);
        cpds.setMaxIdleTime(maxIdleTime);
        cpds.setAcquireIncrement(acquireIncrement);
        cpds.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
    }

    
    public synchronized Connection getConnection() throws SQLException, ClassNotFoundException, InterruptedException
    {
        return cpds.getConnection();
    }
    
    public synchronized DataSource getDataSource()
    {
        return cpds;
    }

    public synchronized void close(Connection conn)
    {
        try
        {
            if (conn != null)
            {
                conn.close();
                conn = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public synchronized void close(Statement stat)
    {
        try
        {
            if (stat != null)
            {
                stat.close();
                stat = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public synchronized void close(ResultSet rest)
    {
        try
        {
            if (rest != null)
            {
                rest.close();
                rest = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testC3P0()
    {
        testC3P0Plugin ds = new testC3P0Plugin();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            Assert.assertNotNull(connection);
            int hashCode1 = connection.hashCode();
            connection.close();
            connection = ds.getConnection();
            int hashCode2 = connection.hashCode();
            
            Assert.assertNotEquals(hashCode1, hashCode2);
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
     
    }
}
