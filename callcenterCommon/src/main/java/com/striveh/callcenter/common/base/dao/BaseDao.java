/** */
package com.striveh.callcenter.common.base.dao;

import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class BaseDao<P extends BasePojo> extends AbsBaseDao<P> {

	/**
	 * @设置 单条sql操作模板
	 */
	@Autowired
	@Qualifier("masterDBSqlSessionTemplate")
	protected void setTemplate(SqlSessionTemplate template) {
		this.template = template;
	}

	/**
	 * @设置 批量sql操作模板
	 */
	@Autowired
	@Qualifier("masterDBSqlSessionBatchTemplate")
	protected void setBatchTemplate(SqlSessionTemplate batchTemplate) {
		this.batchTemplate = batchTemplate;
	}

}
