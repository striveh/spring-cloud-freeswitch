/** */
package com.striveh.callcenter.server.callcenter.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.server.base.BaseController;
import com.striveh.callcenter.server.callcenter.service.iservice.IVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.callcenter.VoicePojo;

/**
 * @功能:【voice 】controller
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-12-18 16:55:21
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/voice")
public class VoiceController extends BaseController<VoicePojo> {
	/** voice service*/
    @Autowired
    private IVoiceService voiceService;
    
  	/**
	 * get
	 * @param voice
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(VoicePojo voice, HttpServletRequest request, HttpServletResponse response) {

	}
}
