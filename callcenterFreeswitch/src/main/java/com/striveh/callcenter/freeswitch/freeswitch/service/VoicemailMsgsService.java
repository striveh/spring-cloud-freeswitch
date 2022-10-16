/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.VoicemailMsgsDao;
import com.striveh.callcenter.pojo.freeswitch.VoicemailMsgsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IVoicemailMsgsService;

/**
 * @功能:【voicemail_msgs 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:32
 * @说明：<pre></pre>
 */
@Service
public class VoicemailMsgsService extends BaseService<VoicemailMsgsPojo, VoicemailMsgsDao> implements IVoicemailMsgsService {

}
