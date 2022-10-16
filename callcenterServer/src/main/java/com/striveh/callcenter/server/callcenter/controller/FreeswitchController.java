/** */
package com.striveh.callcenter.server.callcenter.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.callcenter.FreeswitchPojo;
import com.striveh.callcenter.server.base.BaseController;
import com.striveh.callcenter.server.callcenter.service.iservice.IFreeswitchService;

/**
 * @功能:【freeswitch freeswitch服务实例表】controller
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-05-17 16:54:05
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/freeswitch")
public class FreeswitchController extends BaseController<FreeswitchPojo> {
	/** freeswitch freeswitch服务实例表service*/
    @Autowired
    private IFreeswitchService freeswitchService;
    
  	/**
	 * get
	 * @param freeswitch
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(FreeswitchPojo freeswitch, HttpServletRequest request, HttpServletResponse response) {

	}

	@RequestMapping("/cleanCache")
	public void cleanCache(FreeswitchPojo freeswitch, HttpServletRequest request, HttpServletResponse response) {
		this.freeswitchService.cleanCache(freeswitch);
	}
}
