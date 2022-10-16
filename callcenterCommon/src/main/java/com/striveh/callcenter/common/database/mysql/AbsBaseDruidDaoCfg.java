/** */
package com.striveh.callcenter.common.database.mysql;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public abstract class AbsBaseDruidDaoCfg {
	/**日志*/
	protected Logger logger = LogManager.getLogger(this.getClass());
	/** 数据源参数 */
	private Map<String, String> druidDBCfg = new HashMap<String, String>();
	/** 方法事务配置：什么方法是、否使用事务、何时回滚 */
	private Properties txAttributes = new Properties();
	/** 事务作用的类表达式 */
	private String manageMethod;
	/** mybatis 主配置 */
	private String configLocation;
	/** mybatis sql文件 */
	private String mapperLocations;

	/**
	 * 得到数据源
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract DataSource getDataSource() throws Exception;

	/**
	 * 创建mybatis SqlSessionFactory
	 * 
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	public abstract SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception;

	/**
	 * 得到单条sql语句操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	public abstract SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory);

	/**
	 * 得到批量sql操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	public abstract SqlSessionTemplate getSqlSessionBatchTemplate(SqlSessionFactory sqlSessionFactory);

	/**
	 * 事务管理
	 * 
	 * @param dataSource
	 * @return
	 */
	public abstract PlatformTransactionManager getTransactionManager(DataSource dataSource);

	/**
	 * 事务用在哪些方法拦截器
	 * 
	 * @param txManager
	 * @return
	 */
	public abstract TransactionInterceptor getTransactionInterceptor(PlatformTransactionManager txManager);

	/**
	 * 事务用在哪些类上
	 * 
	 * @param interceptor
	 * @return
	 */
	public abstract DefaultPointcutAdvisor getTransactionDefaultPointcutAdvisor(TransactionInterceptor interceptor);

	/**
	 * 得到数据源
	 * 
	 * @return
	 * @throws Exception
	 */
	protected DataSource generateDataSource() throws Exception {
		logger.info("====================配置操作mysql数据库=====================");
		DataSource ds = DruidDataSourceFactory.createDataSource(druidDBCfg);
		logger.info("DataSource创建成功");
		return ds;
	}

	/**
	 * 创建mybatis SqlSessionFactory
	 * 
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	public SqlSessionFactory generateSqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactory.setMapperLocations(resolver.getResources(mapperLocations));
		sqlSessionFactory.setConfigLocation(resolver.getResource(configLocation));
		sqlSessionFactory.setDataSource(dataSource);
		SqlSessionFactory factory = sqlSessionFactory.getObject();
		logger.info("SqlSessionFactory创建成功");
		return factory;
	}

	/**
	 * 得到单条sql语句操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	public SqlSessionTemplate generateSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		logger.info("SqlSessionTemplate创建成功");
		return sqlSessionTemplate;
	}

	/**
	 * 得到批量sql操作模板
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	public SqlSessionTemplate generateSqlSessionBatchTemplate(SqlSessionFactory sqlSessionFactory) {
		SqlSessionTemplate sqlSessionBatchTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
		logger.info("SqlSessionBatchTemplate创建成功");
		return sqlSessionBatchTemplate;
	}

	/**
	 * 事务管理
	 * 
	 * @param dataSource
	 * @return
	 */
	public PlatformTransactionManager generateTransactionManager(DataSource dataSource) {
		PlatformTransactionManager transactionTxManager = new DataSourceTransactionManager(dataSource);
		logger.info("PlatformTransactionManager事务管理器创建成功");
		return transactionTxManager;
	}

	/**
	 * 事务用在哪些方法拦截器
	 * 
	 * @param txManager
	 * @return
	 */
	public TransactionInterceptor generateTransactionInterceptor(PlatformTransactionManager txManager) {
		TransactionInterceptor transactionTxInterceptor = new TransactionInterceptor(txManager, txAttributes);
		logger.info("TransactionInterceptor事务拦截器创建成功");
		return transactionTxInterceptor;
	}

	/**
	 * 事务用在哪些类上
	 * 
	 * @param interceptor
	 * @return
	 */
	public DefaultPointcutAdvisor generateTransactionDefaultPointcutAdvisor(TransactionInterceptor interceptor) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(manageMethod);
		DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(pointcut, interceptor);
		logger.info("DefaultPointcutAdvisor切面创建成功");
		return defaultPointcutAdvisor;
	}

	/* ========================= get and set ========================== */
	/**
	 * @取得 数据源参数
	 */
	public Map<String, String> getDruidDBCfg() {
		return druidDBCfg;
	}

	/**
	 * @设置 数据源参数
	 */
	public void setDruidDBCfg(Map<String, String> druidDBCfg) {
		this.druidDBCfg = druidDBCfg;
	}

	/**
	 * @取得 方法事务配置：什么方法是、否使用事务、何时回滚
	 */
	public Properties getTxAttributes() {
		return txAttributes;
	}

	/**
	 * @设置 方法事务配置：什么方法是、否使用事务、何时回滚
	 */
	public void setTxAttributes(Properties txAttributes) {
		this.txAttributes = txAttributes;
	}

	/**
	 * @取得 事务作用的类表达式
	 */
	public String getManageMethod() {
		return manageMethod;
	}

	/**
	 * @设置 事务作用的类表达式
	 */
	public void setManageMethod(String manageMethod) {
		this.manageMethod = manageMethod;
	}

	/**
	 * @取得 mybatis主配置
	 */
	public String getConfigLocation() {
		return configLocation;
	}

	/**
	 * @设置 mybatis主配置
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * @取得 mybatissql文件
	 */
	public String getMapperLocations() {
		return mapperLocations;
	}

	/**
	 * @设置 mybatissql文件
	 */
	public void setMapperLocations(String mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

}
