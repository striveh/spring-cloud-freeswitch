/** */
package com.striveh.callcenter.freeswitch.codemaker.dao;


import com.striveh.callcenter.common.base.dao.AbsBaseDao;
import com.striveh.callcenter.freeswitch.codemaker.po.ColumnInfo;
import org.mybatis.spring.SqlSessionTemplate;

public class CodeMakerDao extends AbsBaseDao<ColumnInfo> {
	/**
	 * @设置 单条sql操作模板
	 */
	protected void setTemplate(SqlSessionTemplate template) {
		this.template = template;
	}

	/**
	 * @设置 单条sql操作模板
	 */
	public void setTemplate2(SqlSessionTemplate template) {
		super.template = template;
	}

	/**
	 * @设置 批量sql操作模板
	 */
	protected void setBatchTemplate(SqlSessionTemplate batchTemplate) {
		this.batchTemplate = batchTemplate;
	}

}
