/** */
package com.striveh.callcenter.common.base.service.iservice;

import com.striveh.callcenter.common.base.pojo.BasePojo;

public interface IBaseCacheService<P extends BasePojo, R> extends IBaseService<P> {

	/**
	 * 清理缓存
	 */
	public void cleanCache(P po);

	/**
	 * 取得缓存数据
	 * 
	 * @param po
	 * @return
	 */
	public R getCacheDataByKey(P po);

}
