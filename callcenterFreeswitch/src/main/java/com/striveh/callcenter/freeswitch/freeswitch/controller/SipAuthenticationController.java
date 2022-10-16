/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipAuthenticationPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipAuthenticationService;

/**
 * @功能:【sip_authentication 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipAuthentication")
public class SipAuthenticationController extends BaseController<SipAuthenticationPojo> {
	/** sip_authentication service*/
    @Autowired
    private ISipAuthenticationService sipAuthenticationService;
    
  	/**
	 * get
	 * @param sipAuthentication
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipAuthenticationPojo sipAuthentication, HttpServletRequest request, HttpServletResponse response) {

	}
}
