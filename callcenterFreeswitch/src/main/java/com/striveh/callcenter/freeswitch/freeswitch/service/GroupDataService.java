/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IGroupDataService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.GroupDataDao;
import com.striveh.callcenter.pojo.freeswitch.GroupDataPojo;

/**
 * @功能:【group_data 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@Service
public class GroupDataService extends BaseService<GroupDataPojo, GroupDataDao> implements IGroupDataService {

}
