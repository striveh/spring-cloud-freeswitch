/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IChannelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.ChannelsPojo;

/**
 * @功能:【channels 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/channels")
public class ChannelsController extends BaseController<ChannelsPojo> {
	/** channels service*/
    @Autowired
    private IChannelsService channelsService;
    
  	/**
	 * get
	 * @param channels
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(ChannelsPojo channels, HttpServletRequest request, HttpServletResponse response) {

	}
}
