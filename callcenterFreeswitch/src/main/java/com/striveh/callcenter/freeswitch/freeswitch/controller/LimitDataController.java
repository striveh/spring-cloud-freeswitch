/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.LimitDataPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ILimitDataService;

/**
 * @功能:【limit_data 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/limitData")
public class LimitDataController extends BaseController<LimitDataPojo> {
	/** limit_data service*/
    @Autowired
    private ILimitDataService limitDataService;
    
  	/**
	 * get
	 * @param limitData
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(LimitDataPojo limitData, HttpServletRequest request, HttpServletResponse response) {

	}
}
