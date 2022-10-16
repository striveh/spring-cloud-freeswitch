/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipRegistrationsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipRegistrationsService;

/**
 * @功能:【sip_registrations 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipRegistrations")
public class SipRegistrationsController extends BaseController<SipRegistrationsPojo> {
	/** sip_registrations service*/
    @Autowired
    private ISipRegistrationsService sipRegistrationsService;
    
  	/**
	 * get
	 * @param sipRegistrations
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipRegistrationsPojo sipRegistrations, HttpServletRequest request, HttpServletResponse response) {

	}
}
