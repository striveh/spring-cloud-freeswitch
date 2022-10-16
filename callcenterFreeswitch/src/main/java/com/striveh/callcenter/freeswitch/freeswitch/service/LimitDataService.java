/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ILimitDataService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.LimitDataDao;
import com.striveh.callcenter.pojo.freeswitch.LimitDataPojo;

/**
 * @功能:【limit_data 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@Service
public class LimitDataService extends BaseService<LimitDataPojo, LimitDataDao> implements ILimitDataService {

}
