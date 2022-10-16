/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.RecoveryPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IRecoveryService;

/**
 * @功能:【recovery 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/recovery")
public class RecoveryController extends BaseController<RecoveryPojo> {
	/** recovery service*/
    @Autowired
    private IRecoveryService recoveryService;
    
  	/**
	 * get
	 * @param recovery
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(RecoveryPojo recovery, HttpServletRequest request, HttpServletResponse response) {

	}
}
