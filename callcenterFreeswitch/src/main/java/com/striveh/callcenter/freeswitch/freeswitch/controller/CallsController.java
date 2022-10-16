/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.CallsPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ICallsService;

/**
 * @功能:【calls 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/calls")
public class CallsController extends BaseController<CallsPojo> {
	/** calls service*/
    @Autowired
    private ICallsService callsService;
    
  	/**
	 * get
	 * @param calls
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(CallsPojo calls, HttpServletRequest request, HttpServletResponse response) {

	}
}
