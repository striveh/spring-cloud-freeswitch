/** */
package com.striveh.callcenter.server.callcenter.service;

import com.striveh.callcenter.server.callcenter.dao.FreeswitchDao;
import com.striveh.callcenter.common.base.service.BaseCacheService;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.inbound.option.ServerOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.pojo.callcenter.FreeswitchPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.IFreeswitchService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @功能:【freeswitch freeswitch服务实例表】Service
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-05-17 16:54:05
 * @说明：<pre></pre>
 */
@Service
public class FreeswitchService extends BaseCacheService<FreeswitchPojo, FreeswitchDao> implements IFreeswitchService {

    @Autowired
    private InboundClient inboundClient;
    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;

    private Long cacheLastVer;

    public Map<Long, FreeswitchPojo> freeswitchMap;


    @Override
    public FreeswitchPojo getAvailableServer(FreeswitchPojo freeswitchPojo) {
        return this.dao.selectAvailableServer(freeswitchPojo);
    }


    @Override
    public String getServerAddr(Long id) {
        FreeswitchPojo freeswitchPojo = new FreeswitchPojo(id);
        freeswitchPojo = getCacheDataByKey(freeswitchPojo);
        return freeswitchPojo.getHost()+":"+freeswitchPojo.getPort();
    }

    @PostConstruct
    @Override
    protected void init() {
        Long lastVer = baseRedisDao.get(ERedisCacheKey.KEY_SYS_FREESWITCH_NEW_VER.getCode(), Long.class);
        if (lastVer == null) {
            lastVer = System.currentTimeMillis();
            baseRedisDao.saveOrUpdate(ERedisCacheKey.KEY_SYS_FREESWITCH_NEW_VER.getCode(), lastVer);// 更新新版本为当前
        }
        if (cacheLastVer == null || cacheLastVer < lastVer) {
            logger.info("开始加载freeswitch服务实例列表");
            FreeswitchPojo freeswitchPojo = new FreeswitchPojo();
            freeswitchPojo.setStatus(1);
            cacheLastVer = lastVer;
            Map<Long, FreeswitchPojo> map = new HashMap<Long, FreeswitchPojo>();
            selectList(freeswitchPojo).forEach(freeswitch -> {
                inboundClient.option().addServerOption(new ServerOption(freeswitch.getHost(),freeswitch.getPort()).password(freeswitch.getPassword()));
                map.put(freeswitch.getId(),freeswitch);
            });
            freeswitchMap = map;
            inboundClient.option().addEvents("HEARTBEAT","CHANNEL_CREATE","CHANNEL_PARK","CHANNEL_ANSWER","CHANNEL_UUID","CHANNEL_UNBRIDGE","CHANNEL_BRIDGE",EventNames.CHANNEL_HANGUP,
                    "CHANNEL_HANGUP_COMPLETE","CHANNEL_DESTROY","CHANNEL_ORIGINATE","BACKGROUND_JOB","DTMF", EventNames.PLAYBACK_START,EventNames.PLAYBACK_STOP,EventNames.MEDIA_BUG_STOP,
                    "CUSTOM fifo::info","CUSTOM sofia::register","CUSTOM sofia::unregister","CUSTOM sofia::expire","CUSTOM conference::maintenance");
//            inboundClient.option().addEvents("all");
            logger.info("加载freeswitch服务实例列表完成");
        }
    }

    @Override
    public void cleanCache(FreeswitchPojo po) {
        Long newVer = System.currentTimeMillis();
        baseRedisDao.saveOrUpdate(ERedisCacheKey.KEY_SYS_FREESWITCH_NEW_VER.getCode(), newVer);
        init();
    }

    @Override
    public FreeswitchPojo getCacheDataByKey(FreeswitchPojo po) {
        init();
        return freeswitchMap.get(po.getId());
    }
}
