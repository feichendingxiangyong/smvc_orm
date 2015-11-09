package com.smvc.dao;

import javax.sql.DataSource;

/**
 * plugin for db
 * @author dingxiangyong
 *
 */
public interface IDataSourcePlugin extends IPlugin{
	DataSource getDataSource();
}
