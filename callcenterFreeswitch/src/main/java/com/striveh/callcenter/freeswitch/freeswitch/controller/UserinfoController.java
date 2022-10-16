/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IUserinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;

import java.util.List;

/**
 * @功能:【userinfo 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:32
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/userinfo")
public class UserinfoController extends BaseController<UserinfoPojo> {
	/** userinfo service*/
    @Autowired
    private IUserinfoService userinfoService;
    
  	/**
	 * get
	 * @param userinfo
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(UserinfoPojo userinfo, HttpServletRequest request, HttpServletResponse response) {

	}

	/**************************************************************************************************/
	/**************************************   内部服务调用  *****************************************/
	/**************************************************************************************************/

	@RequestMapping(value = "/inner/addList",method = RequestMethod.POST)
	public void addList(@RequestBody List<UserinfoPojo> userinfoPojos) {
		this.userinfoService.insertList(userinfoPojos);
	}

	@RequestMapping(value = "/inner/add",method = RequestMethod.POST)
	public void add(@RequestBody UserinfoPojo userinfoPojo) {
		this.userinfoService.insert(userinfoPojo);
	}

	@RequestMapping(value = "/inner/update",method = RequestMethod.POST)
	public void update(@RequestBody UserinfoPojo userinfoPojo) {
		this.userinfoService.update(userinfoPojo);
	}

	@RequestMapping(value = "/inner/getByUsername",method = RequestMethod.GET)
	public UserinfoPojo getByUsername (@RequestParam String username){
		UserinfoPojo userinfoPojo=new UserinfoPojo();
		userinfoPojo.setUsername(username);
		return this.userinfoService.selectUnique(userinfoPojo);
	}

	@RequestMapping(value = "/inner/getListByProjectId",method = RequestMethod.GET)
	public List<UserinfoPojo> getListByProjectId (@RequestParam Long projectId){
		UserinfoPojo userinfoPojo=new UserinfoPojo();
		userinfoPojo.setProjectId(projectId);
		return this.userinfoService.selectList(userinfoPojo);
	}


	@RequestMapping(value = "/inner/getListByProjectIdAndStatus",method = RequestMethod.GET)
	public List<UserinfoPojo> getListByProjectIdAndStatus (@RequestParam Long projectId,@RequestParam Integer status){
		UserinfoPojo userinfoPojo=new UserinfoPojo();
		userinfoPojo.setProjectId(projectId);
		userinfoPojo.setStatus(status);
		return this.userinfoService.selectList(userinfoPojo);
	}
}
