package com.smvc.dao.annotation;

/**
 * Temp.java 3:31:49 PM Feb 6, 2012
 *
 * Copyright(c) 2000-2012 HC360.COM, All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * ��װsql �� ִ��sql��Ҫ�Ĳ���
 * 
 * @author dixingxing
 * @date Feb 6, 2012
 */
public class SqlHolder {
    private String sql;
    private List<Object> params = new ArrayList<Object>();

    public void addParam(Object o) {
        params.add(o);
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("sql���:");
        sb.append(sql).append("\r\n").append("             ����ֵ:");
        for (Object obj : params) {
            sb.append(obj).append(",");
        }
        if (params.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
