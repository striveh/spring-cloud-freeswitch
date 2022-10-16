/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.MembersPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IMembersService;

/**
 * @功能:【members 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-13 21:59:26
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/members")
public class MembersController extends BaseController<MembersPojo> {
	/** members service*/
    @Autowired
    private IMembersService membersService;
    
  	/**
	 * get
	 * @param members
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(MembersPojo members, HttpServletRequest request, HttpServletResponse response) {

	}
}
