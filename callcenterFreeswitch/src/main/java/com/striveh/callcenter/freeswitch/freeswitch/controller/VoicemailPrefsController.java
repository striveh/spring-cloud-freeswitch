/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IVoicemailPrefsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.VoicemailPrefsPojo;

/**
 * @功能:【voicemail_prefs 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:32
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/voicemailPrefs")
public class VoicemailPrefsController extends BaseController<VoicemailPrefsPojo> {
	/** voicemail_prefs service*/
    @Autowired
    private IVoicemailPrefsService voicemailPrefsService;
    
  	/**
	 * get
	 * @param voicemailPrefs
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(VoicemailPrefsPojo voicemailPrefs, HttpServletRequest request, HttpServletResponse response) {

	}
}
