/** */
package com.striveh.callcenter.gateway.sys.service;

import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.service.BaseCacheService;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.constant.response.EResCodeCommon;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.gateway.sys.dao.SysAuthInfoDao;
import com.striveh.callcenter.pojo.sys.SysAuthInfoPojo;
import com.striveh.callcenter.gateway.sys.service.iservice.ISysAuthInfoService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysAuthInfoService extends BaseCacheService<SysAuthInfoPojo, SysAuthInfoDao> implements
		ISysAuthInfoService {
	/** redis操作dao */
	@Autowired
	@Qualifier("baseRedisDaoDef")
	private BaseRedisDao baseRedisDao;
	/** 认证超时限制（单位：分钟） */
	private int authValidTime = 5;
	/** 认证信息最新版本 */
	private Long authInfoLastVer;
	/** 认证信息map */
	public Map<String, SysAuthInfoPojo> sysAuthInfoMap;

	/**
	 * 初始化缓存数据，主要通过@PostConstruct注解初始化bean时首次自动加载
	 */
	@Override
	@PostConstruct
	public void init() {
		Long lastVer = baseRedisDao.get(ERedisCacheKey.KEY_SYS_AUTHINFO_NEW_VER.getCode(), Long.class);
		if (lastVer == null) {
			lastVer = System.currentTimeMillis();
			baseRedisDao.saveOrUpdate(ERedisCacheKey.KEY_SYS_AUTHINFO_NEW_VER.getCode(), lastVer);// 更新新版本为当前
		}
		if (authInfoLastVer == null || authInfoLastVer < lastVer) {
			logger.info("====================开始加载AuthInfo信息=====================");
			authInfoLastVer = lastVer;// 记录图片资源对应版本号。
			Map<String, SysAuthInfoPojo> map = new HashMap<String, SysAuthInfoPojo>();
			List<SysAuthInfoPojo> sysAuthInfoList = this.selectList(new SysAuthInfoPojo());
			for (SysAuthInfoPojo authInfo : sysAuthInfoList) {
				map.put(authInfo.getAccount(), authInfo);
			}
			sysAuthInfoMap = map;
			logger.info("加载AuthInfo完成！lastVer:" + lastVer);
		}
	}

	/**
	 * 清理缓存
	 *
	 * @param po
	 */
	@Override
	public void cleanCache(SysAuthInfoPojo po) {
		Long newVer = System.currentTimeMillis();
		baseRedisDao.saveOrUpdate(ERedisCacheKey.KEY_SYS_AUTHINFO_NEW_VER.getCode(), newVer);// 更新新版本为当前
		init();
	}

	/**
	 * 取得缓存数据
	 *
	 * @param sysAuthInfoPojo
	 * @return
	 */
	@Override
	public SysAuthInfoPojo getCacheDataByKey(SysAuthInfoPojo sysAuthInfoPojo) {
		Assert.notNull(sysAuthInfoPojo.getAccount(), "authInfo账号不能为空");
		init();
		return sysAuthInfoMap.get(sysAuthInfoPojo.getAccount());
	}

	/**
	 * 方法重写:检查签名参数、对比签名结果
	 *
	 * @param account
	 * @param timestamp
	 * @param sign
	 */
	@Override
	public OptResult chenkSign(String account, String timestamp, String sign,Integer type) {
		// 检查参数
		if (StringUtils.isEmpty(account)) {
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo账号不能为空。");
		}
		if (StringUtils.isEmpty(timestamp) || !timestamp.matches("^[1-9][0-9]*$")) {
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo请求时间戳不能为空或格式不正确。");
		}
		if (StringUtils.isEmpty(sign)) {
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo签名不能为空。");
		}

		Long signTime = Long.parseLong(timestamp);
		Long s = System.currentTimeMillis() - signTime;
		if (Math.abs(s) > authValidTime * 60000) {
			return EResCodeCommon.svceErrSysAuthInfoTimeOut.getOptResult(logger, "authInfo签名超过" + authValidTime
					+ "分钟,实际：" + (s / 1000) + "秒");
		}

		// 检查账号信息
		SysAuthInfoPojo sysAuthInfoPojo = new SysAuthInfoPojo();
		sysAuthInfoPojo.setAccount(account);
		sysAuthInfoPojo = getCacheDataByKey(sysAuthInfoPojo);
		if (sysAuthInfoPojo == null) {
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo账号无效");
		}
		if (sysAuthInfoPojo.getExpirTime().getTime() < System.currentTimeMillis()) {
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo账号过期。");
		}
		if(!sysAuthInfoPojo.getType().equals(type)){
			return EResCodeCommon.exptSystemException.getOptResult(logger, "authInfo账号非法调用。");
		}

		String newSign = getSign(account, timestamp, sysAuthInfoPojo.getSecret());
		if (newSign.equals(sign)) {
			return EResCodeCommon.svceRigOptSuccess.getOptResult(logger, "authInfo认证通过");
		}
		return EResCodeCommon.svceErrSysAuthInfoTimeOut.getOptResult(logger, "authInfo签名无效");
	}

	/**
	 * 生成签名
	 *
	 * @param account
	 * @param timestamp
	 * @param secret
	 * @return
	 */
	public String getSign(String account, String timestamp, String secret) {
		return StringTool.MD5Encode(account + timestamp + secret);
	}
}
