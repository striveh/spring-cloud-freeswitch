/** */
package com.striveh.callcenter.server.callcenter.service;

import com.striveh.callcenter.server.callcenter.dao.VoiceDao;
import com.striveh.callcenter.common.base.service.BaseCacheService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.pojo.callcenter.VoicePojo;
import com.striveh.callcenter.server.callcenter.service.iservice.IVoiceService;

/**
 * @功能:【voice 】Service
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-12-18 16:55:21
 * @说明：<pre></pre>
 */
@Service
public class VoiceService extends BaseCacheService<VoicePojo, VoiceDao> implements IVoiceService {

    @Override
    protected void init() {

    }

    @Override
    public void cleanCache(VoicePojo po) {

    }

    @Override
    public VoicePojo getCacheDataByKey(VoicePojo po) {
        return null;
    }
}
