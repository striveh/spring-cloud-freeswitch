/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IDbDataService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.DbDataDao;
import com.striveh.callcenter.pojo.freeswitch.DbDataPojo;

/**
 * @功能:【db_data 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@Service
public class DbDataService extends BaseService<DbDataPojo, DbDataDao> implements IDbDataService {

}
