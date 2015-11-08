/**
 * BaseDao.java 7:24:12 PM Jan 17, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */
package com.smvc.dao;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.smvc.dao.annotation.GeneratedKeys;
import com.smvc.dao.annotation.MyBeanProcessor;
import com.smvc.dao.annotation.Parameter;
import com.smvc.dao.annotation.SqlBuilder;
import com.smvc.dao.annotation.SqlHolder;
import com.smvc.dao.util.ClassUtil;

/**
 * dbutils dao 基类
 * 
 * @author dixingxing
 * @date Jan 17, 2012
 */
public class DbPlugin<T> {
    private final static Logger logger = Logger.getLogger(DbPlugin.class);
    private static QueryRunner runner;

    protected static DataSource ds;

    static {
        ds = initDataSource();
        runner = new QueryRunner(ds);
    }

    /**
     * 初始化dhcp数据源
     * 
     * @return
     */
    private static synchronized DataSource initDataSource() {
        String url = "jdbc:mysql://127.0.0.1:3306/test";
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("127.0.0.1");
        // ds.setDatabaseName("testdbutils");
        ds.setURL(url);
        ds.setUser("root");
        ds.setPassword("111111");
        ds.setCharacterEncoding("utf8");
        return ds;
    }

    private final static ScalarHandler scaleHandler = new ScalarHandler() {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            Object obj = super.handle(rs);
            if (obj instanceof BigInteger)
                return ((BigInteger) obj).longValue();

            return obj;
        }
    };

    /**
     * 
     * 使用自定义的 MyBeanProcessor
     * 
     * @see MyBeanProcessor
     * @param clazz
     * @return
     */
    private BeanListHandler<T> getBeanListHandler(Class<T> clazz) {
        return new BeanListHandler<T>(clazz, new BasicRowProcessor(new MyBeanProcessor()));
    }

    /**
     * 使用自定义的 MyBeanProcessor
     * 
     * @see MyBeanProcessor
     * @param clazz
     * @return
     */
    private BeanHandler<T> getBeanHandler(Class<T> clazz) {
        return new BeanHandler<T>(clazz, new BasicRowProcessor(new MyBeanProcessor()));
    }

    /**
     * 从默认的数据源中获取一个数据库连接
     * 
     * @return
     */
    public static Connection getConn() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            logger.error("获取数据库连接失败!", e);
            return null;
        }
    }

    /**
     * 打印日志
     * 
     * @param sql
     */
    private static void showSql(String sql) {
        if (true) {
            logger.debug(sql);
        }
    }

    /**
     * 
     * 查询返回列表
     * 
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    public List<T> queryList(Class<T> clazz, Parameter param) {
        SqlHolder holder = SqlBuilder.buildQuery(clazz, param);
        return this.queryList(holder.getSql(), clazz, holder.getParams());
    }

    /**
     * 
     * 查询返回列表
     * 
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    private List<T> queryList(String sql, Class<T> clazz, Object... params) {
        showSql(sql);
        try {
            return (List<T>) runner.query(sql, getBeanListHandler(clazz), params);
        } catch (SQLException e) {
            logger.debug("查询失败", e);
            return new ArrayList<T>();
        }
    }

    /**
     * 查询返回单个对象
     * 
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    public T query(Class<T> clazz, Parameter param) {
        SqlHolder holder = SqlBuilder.buildQuery(clazz, param);
        showSql(holder.getSql());
        try {
            return (T) runner.query(holder.getSql(), getBeanHandler(clazz), holder.getParams());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回long型数据
     * 
     * @param sql
     * @param params
     * @return
     */
    public Long queryLong(String sql, Object... params) {
        showSql(sql);
        try {
            Number n = (Number) runner.query(sql, scaleHandler, params);
            return n.longValue();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int save(Object obj) {
        SqlHolder holder = SqlBuilder.buildInsert(obj);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    public long saveWithGeneratedKeys(Object obj) throws IllegalArgumentException, IllegalAccessException {
        SqlHolder holder = SqlBuilder.buildInsert(obj);

        // generated primary key
        Long generatedKey = this.insert(DbPlugin.getConn(), holder.getSql(), holder.getParams());

        // set key value to object
        Class<?> clazz = obj.getClass();
        Field[] fields = ClassUtil.getAnnotatedDeclaredFields(clazz, GeneratedKeys.class, true);

        if (fields.length > 0) {
            // forced access
            fields[0].setAccessible(true);

            // set value
            fields[0].set(obj, generatedKey);

        }

        return generatedKey;
    }

    public int delete(Class<?> clazz, String where) {
        SqlHolder holder = SqlBuilder.buildDelete(clazz, where);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    public int delete(Class<?> clazz, Parameter param) {
        SqlHolder holder = SqlBuilder.buildDelete(clazz, param);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    public int update(Object obj, String where) {
        SqlHolder holder = SqlBuilder.buildUpdate(obj, where);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    public int update(Object obj, Parameter param) {
        SqlHolder holder = SqlBuilder.buildUpdate(obj, param);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    /**
     * 执行INSERT/UPDATE/DELETE语句
     * 
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    private int update(Connection conn, String sql, Object... params) {
        showSql(sql);
        try {

            return runner.update(conn, sql, params);
        } catch (SQLException e) {
            logger.debug("更新操作失败", e);
            return 0;
        }
    }

    /**
     * 执行INSERT语句
     * 
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    private Long insert(Connection conn, String sql, Object... params) {
        showSql(sql);
        try {
            return runner.insert(conn, sql, new ScalarHandler<Long>(), params);
        } catch (SQLException e) {
            logger.debug("更新操作失败", e);
            return new Long(0);
        }
    }

    /**
     * 
     * 查询列表
     * 
     * @param sql
     * @return Map<String, Object>
     */
    public List<Map<String, Object>> queryList(String sql) {
        showSql(sql);
        try {
            List<Map<String, Object>> results = (List<Map<String, Object>>) runner.query(sql, new MapListHandler());
            return results;
        } catch (SQLException e) {
            logger.error("查询失败", e);
            return new ArrayList<Map<String, Object>>();
        }
    }

}
