/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.RegistrationsDao;
import com.striveh.callcenter.pojo.freeswitch.RegistrationsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IRegistrationsService;

/**
 * @功能:【registrations 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@Service
public class RegistrationsService extends BaseService<RegistrationsPojo, RegistrationsDao> implements IRegistrationsService {

}
