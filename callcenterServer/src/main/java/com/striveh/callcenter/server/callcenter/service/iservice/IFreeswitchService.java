/** */
package com.striveh.callcenter.server.callcenter.service.iservice;

import com.striveh.callcenter.common.base.service.iservice.IBaseCacheService;
import com.striveh.callcenter.pojo.callcenter.FreeswitchPojo;

/**
 * @功能:【freeswitch freeswitch服务实例表】IService
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-05-17 16:54:05
 * @说明：<pre></pre>
 */
public interface IFreeswitchService extends IBaseCacheService<FreeswitchPojo,FreeswitchPojo> {

    public FreeswitchPojo getAvailableServer(FreeswitchPojo freeswitchPojo);
    String getServerAddr(Long id);
}
