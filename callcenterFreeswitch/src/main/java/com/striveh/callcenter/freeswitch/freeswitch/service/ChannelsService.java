/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IChannelsService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.ChannelsDao;
import com.striveh.callcenter.pojo.freeswitch.ChannelsPojo;

/**
 * @功能:【channels 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@Service
public class ChannelsService extends BaseService<ChannelsPojo, ChannelsDao> implements IChannelsService {

}
