/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ITiersService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.TiersDao;
import com.striveh.callcenter.pojo.freeswitch.TiersPojo;

/**
 * @功能:【tiers 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-13 21:59:26
 * @说明：<pre></pre>
 */
@Service
public class TiersService extends BaseService<TiersPojo, TiersDao> implements ITiersService {

}
