/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.GroupDataPojo;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IGroupDataService;

/**
 * @功能:【group_data 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/groupData")
public class GroupDataController extends BaseController<GroupDataPojo> {
	/** group_data service*/
    @Autowired
    private IGroupDataService groupDataService;
    
  	/**
	 * get
	 * @param groupData
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(GroupDataPojo groupData, HttpServletRequest request, HttpServletResponse response) {

	}
}
