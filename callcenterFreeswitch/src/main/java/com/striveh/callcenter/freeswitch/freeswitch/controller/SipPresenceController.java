/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipPresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipPresencePojo;

/**
 * @功能:【sip_presence 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipPresence")
public class SipPresenceController extends BaseController<SipPresencePojo> {
	/** sip_presence service*/
    @Autowired
    private ISipPresenceService sipPresenceService;
    
  	/**
	 * get
	 * @param sipPresence
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipPresencePojo sipPresence, HttpServletRequest request, HttpServletResponse response) {

	}
}
