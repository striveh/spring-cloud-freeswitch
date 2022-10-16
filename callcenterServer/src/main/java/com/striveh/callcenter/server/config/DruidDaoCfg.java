/** */
package com.striveh.callcenter.server.config;

import com.striveh.callcenter.common.database.mysql.AbsBaseDruidDaoCfg;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "master-db.db-cfg")
public class DruidDaoCfg extends AbsBaseDruidDaoCfg {
	/**
	 * 得到数据源
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	@Bean(name = "masterDBDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource getDataSource() throws Exception {
		return super.generateDataSource();
	}

	/**
	 * 创建mybatis SqlSessionFactory
	 * 
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	@Override
	@Bean(name = "masterDBSqlSessionFactory")
	public SqlSessionFactory getSqlSessionFactory(@Qualifier("masterDBDataSource") DataSource dataSource)
			throws Exception {
		return super.generateSqlSessionFactory(dataSource);
	}

	/**
	 * 得到单条sql语句操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	@Override
	@Bean(name = "masterDBSqlSessionTemplate")
	public SqlSessionTemplate getSqlSessionTemplate(
			@Qualifier("masterDBSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return super.generateSqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * 得到批量sql操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	@Override
	@Bean(name = "masterDBSqlSessionBatchTemplate")
	public SqlSessionTemplate getSqlSessionBatchTemplate(
			@Qualifier("masterDBSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return super.generateSqlSessionBatchTemplate(sqlSessionFactory);
	}

	/**
	 * 事务管理
	 * 
	 * @param dataSource
	 * @return
	 */
	@Override
	@Bean(name = "masterDBTxManager")
	public PlatformTransactionManager getTransactionManager(@Qualifier("masterDBDataSource") DataSource dataSource) {
		return super.generateTransactionManager(dataSource);
	}

	/**
	 * 事务用在哪些方法拦截器
	 * 
	 * @param txManager
	 * @return
	 */
	@Override
	@Bean(name = "masterDBTxInterceptor")
	public TransactionInterceptor getTransactionInterceptor(
			@Qualifier("masterDBTxManager") PlatformTransactionManager txManager) {
		return super.generateTransactionInterceptor(txManager);
	}

	/**
	 * 事务用在哪些类上
	 * 
	 * @param interceptor
	 * @return
	 */
	@Override
	@Bean(name = "masterDBTxAop")
	public DefaultPointcutAdvisor getTransactionDefaultPointcutAdvisor(
			@Qualifier("masterDBTxInterceptor") TransactionInterceptor interceptor) {
		return super.generateTransactionDefaultPointcutAdvisor(interceptor);
	}

}
