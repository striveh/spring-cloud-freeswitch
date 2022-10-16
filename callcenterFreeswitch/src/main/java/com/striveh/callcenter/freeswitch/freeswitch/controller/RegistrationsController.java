/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.RegistrationsPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IRegistrationsService;

/**
 * @功能:【registrations 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationsController extends BaseController<RegistrationsPojo> {
	/** registrations service*/
    @Autowired
    private IRegistrationsService registrationsService;
    
  	/**
	 * get
	 * @param registrations
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(RegistrationsPojo registrations, HttpServletRequest request, HttpServletResponse response) {

	}
}
