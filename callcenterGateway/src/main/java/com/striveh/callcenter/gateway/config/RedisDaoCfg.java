/** */
package com.striveh.callcenter.gateway.config;

import com.striveh.callcenter.common.database.redis.AbsBaseRedisCfg;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "master-db.redis-cfg")
public class RedisDaoCfg extends AbsBaseRedisCfg {

	/** 默认保存dbIndex */
	public final int defaultDBIIndex = 14;
	/** 队列保存dbIndex */
	public final int queueDBIndex = 15;

	/**
	 *
	 * 默认保存 缓存
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "baseRedisDaoDef")
	public BaseRedisDao getBaseRedisDao() throws Exception {
		return this.generateBaseRedisDao(defaultDBIIndex);
	}

	/**
	 * 默认队列缓存
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "baseRedisDaoDBCache")
	public BaseRedisDao getBaseRedisDao6() throws Exception {
		return this.generateBaseRedisDao(queueDBIndex);
	}

}
