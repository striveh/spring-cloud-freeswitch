/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.AliasesPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IAliasesService;

/**
 * @功能:【aliases 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/aliases")
public class AliasesController extends BaseController<AliasesPojo> {
	/** aliases service*/
    @Autowired
    private IAliasesService aliasesService;
    
  	/**
	 * get
	 * @param aliases
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(AliasesPojo aliases, HttpServletRequest request, HttpServletResponse response) {

	}
}
