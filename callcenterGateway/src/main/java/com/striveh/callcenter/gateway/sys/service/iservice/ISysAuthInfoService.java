/** */
package com.striveh.callcenter.gateway.sys.service.iservice;

import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.service.iservice.IBaseCacheService;
import com.striveh.callcenter.pojo.sys.SysAuthInfoPojo;

public interface ISysAuthInfoService extends IBaseCacheService<SysAuthInfoPojo, SysAuthInfoPojo> {
	/**
	 * 验证签名是否正确
	 *
	 * @param account
	 * @param timestamp
	 * @param sign
	 * @return
	 */
	public OptResult chenkSign(String account, String timestamp, String sign,Integer type);

	/**
	 * 生成签名
	 *
	 * @param account
	 * @param timestamp
	 * @param secret
	 * @return
	 */
	public String getSign(String account, String timestamp, String secret);
}
