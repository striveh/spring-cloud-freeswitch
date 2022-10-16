/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.MembersDao;
import com.striveh.callcenter.pojo.freeswitch.MembersPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IMembersService;

/**
 * @功能:【members 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-13 21:59:26
 * @说明：<pre></pre>
 */
@Service
public class MembersService extends BaseService<MembersPojo, MembersDao> implements IMembersService {

}
