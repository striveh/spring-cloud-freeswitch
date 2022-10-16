/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IAgentsService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.AgentsDao;
import com.striveh.callcenter.pojo.freeswitch.AgentsPojo;

/**
 * @功能:【agents 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-13 21:59:25
 * @说明：<pre></pre>
 */
@Service
public class AgentsService extends BaseService<AgentsPojo, AgentsDao> implements IAgentsService {

}
