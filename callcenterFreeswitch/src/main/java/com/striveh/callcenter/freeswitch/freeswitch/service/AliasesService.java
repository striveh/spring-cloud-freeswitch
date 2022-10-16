/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.AliasesDao;
import com.striveh.callcenter.pojo.freeswitch.AliasesPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IAliasesService;

/**
 * @功能:【aliases 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@Service
public class AliasesService extends BaseService<AliasesPojo, AliasesDao> implements IAliasesService {

}
