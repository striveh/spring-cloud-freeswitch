/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.AgentsPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IAgentsService;

/**
 * @功能:【agents 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-13 21:59:25
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/agents")
public class AgentsController extends BaseController<AgentsPojo> {
	/** agents service*/
    @Autowired
    private IAgentsService agentsService;
    
  	/**
	 * get
	 * @param agents
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(AgentsPojo agents, HttpServletRequest request, HttpServletResponse response) {

	}
}
