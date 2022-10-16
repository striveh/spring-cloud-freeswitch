/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ICompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.CompletePojo;

/**
 * @功能:【complete 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/complete")
public class CompleteController extends BaseController<CompletePojo> {
	/** complete service*/
    @Autowired
    private ICompleteService completeService;
    
  	/**
	 * get
	 * @param complete
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(CompletePojo complete, HttpServletRequest request, HttpServletResponse response) {

	}
}
