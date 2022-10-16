/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.NatPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.INatService;

/**
 * @功能:【nat 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/nat")
public class NatController extends BaseController<NatPojo> {
	/** nat service*/
    @Autowired
    private INatService natService;
    
  	/**
	 * get
	 * @param nat
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(NatPojo nat, HttpServletRequest request, HttpServletResponse response) {

	}
}
