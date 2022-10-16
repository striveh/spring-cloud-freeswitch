/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.TasksPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ITasksService;

/**
 * @功能:【tasks 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/tasks")
public class TasksController extends BaseController<TasksPojo> {
	/** tasks service*/
    @Autowired
    private ITasksService tasksService;
    
  	/**
	 * get
	 * @param tasks
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(TasksPojo tasks, HttpServletRequest request, HttpServletResponse response) {

	}
}
