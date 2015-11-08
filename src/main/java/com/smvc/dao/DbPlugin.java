
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
 * Base dao
 * 
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
     * init dhcp data source
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
     * Use handler of MyBeanProcessor
     * 
     * @see MyBeanProcessor
     * @param clazz
     * @return
     */
    private BeanListHandler<T> getBeanListHandler(Class<T> clazz) {
        return new BeanListHandler<T>(clazz, new BasicRowProcessor(new MyBeanProcessor()));
    }

    /**
     * Use handler of MyBeanProcessor
     * 
     * @see MyBeanProcessor
     * @param clazz
     * @return
     */
    private BeanHandler<T> getBeanHandler(Class<T> clazz) {
        return new BeanHandler<T>(clazz, new BasicRowProcessor(new MyBeanProcessor()));
    }

    /**
     * Get one connection
     * 
     * @return
     */
    public static Connection getConn() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            logger.error("Get db connecttion failed!", e);
            return null;
        }
    }

    /**
     * print sql
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
     * query list 
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
     * query all of rows with returning list
     * 
     * @param sql
     * @param clazz
     * @param params
     * @return
     */
    public List<T> queryAllList(Class<T> clazz) {
        return this.queryList(clazz, null);
    }

    /**
     * 
     * query rows with returning list
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
            logger.debug("query failed.", e);
            return new ArrayList<T>();
        }
    }

    /**
     * query one object
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
     * query long
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

    /**
     * Save one object
     * @param obj
     * @return
     */
    public int save(Object obj) {
        SqlHolder holder = SqlBuilder.buildInsert(obj);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }
    
    /**
     * Save a list of objects
     * @param obj 
     * @return
     */
    public <T> int[] saveList(List<T> objs) {
        SqlHolder holder = SqlBuilder.buildInsertList(objs);
        Object[][] params = new Object[holder.getParams().length][];
        int index = 0;
        for (Object item : holder.getParams())
        {
            params[index ++] = (Object[])item;
        }
        return this.insertList(DbPlugin.getConn(), holder.getSql(), params);
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

    public int delete(Class<T> clazz, String where) {
        SqlHolder holder = SqlBuilder.buildDelete(clazz, where);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }

    public int delete(Class<?> clazz, Parameter param) {
        SqlHolder holder = SqlBuilder.buildDelete(clazz, param);
        return this.update(DbPlugin.getConn(), holder.getSql(), holder.getParams());
    }
    
    public <T> int deleteAll(Class<T> clazz) {
        return delete(clazz, null);
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
     * execute INSERT/UPDATE/DELETE 
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
            logger.debug("update failed!", e);
            return 0;
        }
    }

    /**
     * execute INSERT
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
            logger.debug("update failed!", e);
            return new Long(0);
        }
    }

    /**
     * execute INSERT
     * 
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    private int[] insertList(Connection conn, String sql, Object[][] params) {
        showSql(sql);
        try {
            return runner.batch(conn, sql, params);
        } catch (SQLException e) {
            logger.debug("update failed!", e);
            return new int[0];
        }
    }
    
    /**
     * 
     * query list
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
            logger.error("query failed!", e);
            return new ArrayList<Map<String, Object>>();
        }
    }

}
