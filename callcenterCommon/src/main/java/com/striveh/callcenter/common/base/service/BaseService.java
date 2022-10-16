/** */
package com.striveh.callcenter.common.base.service;

import com.striveh.callcenter.common.base.dao.AbsBaseDao;
import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService<P extends BasePojo, D extends AbsBaseDao<P>> extends AbsBaseService<P, D> {
	/**
	 * @设置 dao工具
	 */
	@Autowired
	protected void setDao(D dao) {
		this.dao = dao;
	}

}
