/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.VoicemailPrefsDao;
import com.striveh.callcenter.pojo.freeswitch.VoicemailPrefsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IVoicemailPrefsService;

/**
 * @功能:【voicemail_prefs 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:32
 * @说明：<pre></pre>
 */
@Service
public class VoicemailPrefsService extends BaseService<VoicemailPrefsPojo, VoicemailPrefsDao> implements IVoicemailPrefsService {

}
