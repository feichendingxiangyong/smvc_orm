package com.smvc.dao;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Plugin
{
    private ComboPooledDataSource ds = null;
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass = "com.mysql.jdbc.Driver";
    private int maxPoolSize = 100;
    private int minPoolSize = 10;
    private int initialPoolSize = 10;
    private int maxIdleTime = 20;
    private int acquireIncrement = 2;
    
    public C3P0Plugin(String jdbcUrl, String user, String password) {
        
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        
        ds = new ComboPooledDataSource();
        try
        {
            ds.setDriverClass(driverClass);
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }           
        ds.setJdbcUrl(this.jdbcUrl);
        ds.setUser(this.user);                                  
        ds.setPassword(this.password);
        ds.setMaxPoolSize(maxPoolSize);
        ds.setMinPoolSize(minPoolSize);
        ds.setInitialPoolSize(initialPoolSize);
        ds.setMaxIdleTime(maxIdleTime);
        ds.setAcquireIncrement(acquireIncrement);

    }

    public synchronized DataSource getDataSource()
    {
        return ds;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public int getAcquireIncrement() {
        return acquireIncrement;
    }

    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }
    
}
