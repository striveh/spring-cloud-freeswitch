/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.VoicemailMsgsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IVoicemailMsgsService;

/**
 * @功能:【voicemail_msgs 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:32
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/voicemailMsgs")
public class VoicemailMsgsController extends BaseController<VoicemailMsgsPojo> {
	/** voicemail_msgs service*/
    @Autowired
    private IVoicemailMsgsService voicemailMsgsService;
    
  	/**
	 * get
	 * @param voicemailMsgs
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(VoicemailMsgsPojo voicemailMsgs, HttpServletRequest request, HttpServletResponse response) {

	}
}
