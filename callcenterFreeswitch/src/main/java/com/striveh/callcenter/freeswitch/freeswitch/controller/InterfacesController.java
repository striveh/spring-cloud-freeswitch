/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.InterfacesPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IInterfacesService;

/**
 * @功能:【interfaces 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/interfaces")
public class InterfacesController extends BaseController<InterfacesPojo> {
	/** interfaces service*/
    @Autowired
    private IInterfacesService interfacesService;
    
  	/**
	 * get
	 * @param interfaces
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(InterfacesPojo interfaces, HttpServletRequest request, HttpServletResponse response) {

	}
}
