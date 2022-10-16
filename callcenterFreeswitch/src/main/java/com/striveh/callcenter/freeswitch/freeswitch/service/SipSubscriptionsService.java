/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipSubscriptionsService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.SipSubscriptionsDao;
import com.striveh.callcenter.pojo.freeswitch.SipSubscriptionsPojo;

/**
 * @功能:【sip_subscriptions 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@Service
public class SipSubscriptionsService extends BaseService<SipSubscriptionsPojo, SipSubscriptionsDao> implements ISipSubscriptionsService {

}
