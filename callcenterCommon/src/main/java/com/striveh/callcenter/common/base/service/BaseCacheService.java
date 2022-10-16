/** */
package com.striveh.callcenter.common.base.service;

import com.striveh.callcenter.common.base.dao.AbsBaseDao;
import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseCacheService<P extends BasePojo, D extends AbsBaseDao<P>> extends AbsBaseCacheService<P, D> {
	/**
	 * @设置 dao工具
	 */
	@Autowired
	protected void setDao(D dao) {
		this.dao = dao;
	}
}
